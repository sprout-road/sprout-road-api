package com.strout.api.admin;

import com.strout.api.gis.application.SidoGisService;
import com.strout.api.gis.application.SigunguGisService;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminController {

    private final SidoGisService sidoGisService;
    private final SigunguGisService sigunguGisService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "admin/upload";
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
}
