package com.strout.api.gis.ui.web;

import com.strout.api.gis.application.GisUploadService;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import com.strout.api.gis.domain.ShapefileType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
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
public class GisController {

    private static final String UPLOAD_BASE_DIR = System.getProperty("java.io.tmpdir") + "/shapefile_upload/";

    private final GisUploadService gisUploadService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "admin/upload";
    }

    /**
     * 시/도 Shapefile 업로드 처리
     */
    @PostMapping("/upload/shapefile/sido")
    public String uploadSidoShapefile(
        @RequestParam("shpFile") MultipartFile shpFile,
        @RequestParam("dbfFile") MultipartFile dbfFile,
        @RequestParam("shxFile") MultipartFile shxFile,
        @RequestParam("prjFile") MultipartFile prjFile,
        Model model
    ) {
        ShapefileUploadCommand command = getShapefileUploadCommand(shpFile, dbfFile, shxFile, prjFile);
        gisUploadService.uploadSidoShapefile(command, ShapefileType.SIDO);

        model.addAttribute("message", "시/도 데이터 업로드가 완료되었습니다! (17개 광역시도)");
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

    /**
     * 시/군/구 Shapefile 업로드 처리
     */
    @PostMapping("/upload/shapefile/sigungu")
    public String uploadSigunguShapefile(
        @RequestParam("shpFile") MultipartFile shpFile,
        @RequestParam("dbfFile") MultipartFile dbfFile,
        @RequestParam("shxFile") MultipartFile shxFile,
        @RequestParam("prjFile") MultipartFile prjFile,
        Model model
    ) {
        try {
            log.info("=== 시/군/구 Shapefile 업로드 시작 ===");

            // 파일 검증
            validateShapefiles(shpFile, dbfFile, shxFile, prjFile);

            // 파일 정보 로깅
            logFileInfo(shpFile, "시/군/구 SHP");
            logFileInfo(dbfFile, "시/군/구 DBF");
            logFileInfo(shxFile, "시/군/구 SHX");
            logFileInfo(prjFile, "시/군/구 PRJ");

            // 임시 저장
            String shpFilePath = saveTemporaryFiles("sigungu", shpFile, dbfFile, shxFile, prjFile);

            // 시/군/구 데이터 파싱 및 로깅
            parseSigunguData(shpFilePath);

            log.info("=== 시/군/구 Shapefile 업로드 완료 ===");
            model.addAttribute("message", "시/군/구 데이터 업로드가 완료되었습니다! (252개 시군구)");
            model.addAttribute("success", true);

        } catch (Exception e) {
            log.error("시/군/구 Shapefile 업로드 실패: {}", e.getMessage(), e);
            model.addAttribute("message", "시/군/구 업로드 실패: " + e.getMessage());
            model.addAttribute("success", false);
        }

        return "admin/upload";
    }

    /**
     * 파일 유효성 검증
     */
    private void validateShapefiles(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("모든 파일을 업로드해주세요.");
            }

            // 파일 크기 제한 (50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 50MB를 초과할 수 없습니다: " + file.getOriginalFilename());
            }
        }
    }

    /**
     * 파일 정보 로깅
     */
    private void logFileInfo(MultipartFile file, String type) {
        log.info("{} 파일 정보:", type);
        log.info("  - 파일명: {}", file.getOriginalFilename());
        log.info("  - 크기: {} bytes ({} KB)", file.getSize(), file.getSize() / 1024);
        log.info("  - Content-Type: {}", file.getContentType());
    }

    /**
     * 임시 파일 저장
     */
    private String saveTemporaryFiles(String dataType, MultipartFile... files) throws IOException {
        String uploadDir = UPLOAD_BASE_DIR + dataType + "_" + System.currentTimeMillis() + "/";
        Files.createDirectories(Paths.get(uploadDir));

        String shpFilePath = null;

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir + filename);
            file.transferTo(targetPath);
            log.info("임시 저장: {}", targetPath.toString());

            // SHP 파일 경로 저장
            if (filename != null && filename.toLowerCase().endsWith(".shp")) {
                shpFilePath = targetPath.toString();
            }
        }

        if (shpFilePath == null) {
            throw new IllegalArgumentException("SHP 파일을 찾을 수 없습니다.");
        }

        return shpFilePath;
    }

    /**
     * 시/군/구 데이터 파싱
     */
    private void parseSigunguData(String shpFilePath) {
        try {
            File shpFile = new File(shpFilePath);
            ShapefileDataStore dataStore = new ShapefileDataStore(shpFile.toURI().toURL());
            dataStore.setCharset(Charset.forName("EUC-KR"));

            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureType schema = featureSource.getSchema();

            log.info("=== 시/군/구 Shapefile 데이터 분석 ===");
            log.info("Feature Type: {}", schema.getTypeName());
            log.info("좌표계: {}", schema.getCoordinateReferenceSystem());
            log.info("속성 개수: {}", schema.getAttributeCount());

            // 속성 정보 로깅
            log.info("=== 시/군/구 속성 정보 ===");
            for (AttributeDescriptor attr : schema.getAttributeDescriptors()) {
                log.info("- {}: {} ({})",
                    attr.getLocalName(),
                    attr.getType().getBinding().getSimpleName(),
                    attr.getType().getName());
            }

            SimpleFeatureCollection features = featureSource.getFeatures();
            log.info("총 시/군/구 개수: {}", features.size());

            // 시/도별 그룹화 분석
            Map<String, List<SigunguInfo>> provinceGroups = new HashMap<>();

            try (SimpleFeatureIterator iterator = features.features()) {
                int count = 0;
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    String sigCode = (String) feature.getAttribute("SIG_CD");
                    String sigKorName = (String) feature.getAttribute("SIG_KOR_NM");
                    String sigEngName = (String) feature.getAttribute("SIG_ENG_NM");

                    if (sigCode != null && sigCode.length() >= 2) {
                        String provinceCode = sigCode.substring(0, 2);
                        SigunguInfo info = new SigunguInfo(sigCode, sigKorName, sigEngName);

                        provinceGroups.computeIfAbsent(provinceCode, k -> new ArrayList<>()).add(info);

                        // 처음 5개만 상세 로깅
                        if (count < 5) {
                            log.info("=== 시/군/구 {} ===", count + 1);
                            log.info("  코드: {} (시/도: {})", sigCode, provinceCode);
                            log.info("  한글명: {}", sigKorName);
                            log.info("  영문명: {}", sigEngName);

                            Geometry geom = (Geometry) feature.getAttribute("the_geom");
                            if (geom != null) {
                                log.info("  기하정보: {} (좌표 개수: {})", geom.getGeometryType(), geom.getNumPoints());
                                log.info("  좌표 범위: {}", geom.getEnvelopeInternal());
                            }
                        }
                        count++;
                    }
                }
            }

            // 시/도별 그룹화 결과 로깅
            log.info("=== 시/도별 시/군/구 그룹화 결과 ===");
            provinceGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String provinceCode = entry.getKey();
                    List<SigunguInfo> districts = entry.getValue();

                    log.info("시/도 코드 {}: {}개 시/군/구", provinceCode, districts.size());

                    // 처음 3개만 표시
                    districts.stream().limit(3).forEach(info ->
                        log.info("  - {} ({})", info.korName, info.code));

                    if (districts.size() > 3) {
                        log.info("  ... 외 {}개", districts.size() - 3);
                    }
                });

            dataStore.dispose();
            log.info("=== 시/군/구 데이터 분석 완료 ===");

        } catch (Exception e) {
            log.error("시/군/구 Shapefile 파싱 오류: {}", e.getMessage(), e);
            throw new RuntimeException("시/군/구 Shapefile 파싱 실패", e);
        }
    }

    /**
     * 시/군/구 정보 DTO
     */
    private static class SigunguInfo {
        final String code;
        final String korName;
        final String engName;

        SigunguInfo(String code, String korName, String engName) {
            this.code = code;
            this.korName = korName;
            this.engName = engName;
        }
    }
}