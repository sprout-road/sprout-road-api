package com.sprout.api.admin;

import com.sprout.api.admin.docs.AdminControllerDocs;
import com.sprout.api.gis.application.SidoGisService;
import com.sprout.api.gis.application.SigunguGisService;
import com.sprout.api.gis.application.command.ShapefileUploadCommand;
import com.sprout.api.gis.application.command.dto.ShapefileDto;
import com.sprout.api.gis.domain.SidoRepository;
import com.sprout.api.gis.domain.SigunguRepository;
import com.sprout.api.gis.infrastructure.jpa.SidoJpaRepository;
import com.sprout.api.gis.infrastructure.jpa.SigunguJpaRepository;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminController implements AdminControllerDocs {

    private final SidoGisService sidoGisService;
    private final SigunguGisService sigunguGisService;
    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;

    @GetMapping("/upload")
    public String uploadPage() {
        return "admin/upload";
    }

    @GetMapping("/map-test")
    public String getTestPage() {
        return "admin/map-test";
    }

    @PostMapping("/upload/shapefile/sido")
    public String uploadSidoShapefile(
        @RequestParam("shpFile") MultipartFile shpFile,
        @RequestParam("dbfFile") MultipartFile dbfFile,
        @RequestParam("shxFile") MultipartFile shxFile,
        @RequestParam("prjFile") MultipartFile prjFile,
        Model model
    ) {
        ShapefileUploadCommand command = getShapefileUploadCommand(shpFile, dbfFile, shxFile, prjFile);
        sidoGisService.uploadSidoShapefile(command);

        model.addAttribute("message", "시/도 데이터 업로드가 완료되었습니다! (17개 광역시도)");
        model.addAttribute("success", true);

        return "admin/upload";
    }

    @PostMapping("/upload/shapefile/sigungu")
    public String uploadSigunguShapefile(
        @RequestParam("shpFile") MultipartFile shpFile,
        @RequestParam("dbfFile") MultipartFile dbfFile,
        @RequestParam("shxFile") MultipartFile shxFile,
        @RequestParam("prjFile") MultipartFile prjFile,
        Model model
    ) {
        ShapefileUploadCommand command = getShapefileUploadCommand(shpFile, dbfFile, shxFile, prjFile);
        sigunguGisService.uploadSigunguShapefile(command);

        model.addAttribute("message", "시/군/구 데이터 업로드가 완료되었습니다! (252개 시군구)");
        model.addAttribute("success", true);

        return "admin/upload";
    }

    private static ShapefileUploadCommand getShapefileUploadCommand(
        MultipartFile shpFile,
        MultipartFile dbfFile,
        MultipartFile shxFile,
        MultipartFile prjFile
    ) {
        try {
            return new ShapefileUploadCommand(
                new ShapefileDto(shpFile.getBytes(), shpFile.getOriginalFilename()),
                new ShapefileDto(dbfFile.getBytes(), dbfFile.getOriginalFilename()),
                new ShapefileDto(shxFile.getBytes(), shxFile.getOriginalFilename()),
                new ShapefileDto(prjFile.getBytes(), prjFile.getOriginalFilename())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @GetMapping("/sido")
    public ResponseEntity<byte[]> getAllSido() {
        log.info(" =========> sido ");
        String json = sidoRepository.findAllAsGeoJson();
        return createJsonDownloadResponse(json, "sido.json");
    }

    @ResponseBody
    @GetMapping("/sigungu/{sidoCode}")
    public ResponseEntity<byte[]> getSigunguBySidoCode(@PathVariable @Pattern(regexp = "\\d{2}") String sidoCode) {
        String json = sigunguRepository.findBySidoCodeAsGeoJson(sidoCode);
        return createJsonDownloadResponse(json, "sigungu_" + sidoCode + ".json");
    }

    @ResponseBody
    @GetMapping("/sido/{sidoCode}/boundaries")
    public ResponseEntity<byte[]> getSidoBoundaries(@PathVariable @Pattern(regexp = "\\d{2}") String sidoCode) {
        String json = sidoRepository.findSidoBoundaries(sidoCode);
        return createJsonDownloadResponse(json, "sido_boundary_" + sidoCode + ".json");
    }

    private ResponseEntity<byte[]> createJsonDownloadResponse(String json, String filename) {
        if (json == null || json.trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(bytes);
    }
}
