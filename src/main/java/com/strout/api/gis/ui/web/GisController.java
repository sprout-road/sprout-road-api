package com.strout.api.gis.ui.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.Property;
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
public class GisController {

    @GetMapping("/upload")
    public String uploadPage() {
        return "admin/upload";
    }

    @PostMapping("/upload/shapefile")
    public String uploadShapefile(
        @RequestParam("shpFile") MultipartFile shpFile,
        @RequestParam("dbfFile") MultipartFile dbfFile,
        @RequestParam("shxFile") MultipartFile shxFile,
        @RequestParam("prjFile") MultipartFile prjFile,
        Model model
    ) {
        try {
            log.info("=== Shapefile 업로드 시작 ===");

            // 파일 검증
            validateShapefiles(shpFile, dbfFile, shxFile, prjFile);

            // 파일 정보 로깅
            logFileInfo(shpFile, "SHP");
            logFileInfo(dbfFile, "DBF");
            logFileInfo(shxFile, "SHX");
            logFileInfo(prjFile, "PRJ");

            // 임시 저장
            String shpFilePath = saveTemporaryFiles(shpFile, dbfFile, shxFile, prjFile);

            // Shapefile 데이터 파싱 및 로깅
            parseAndLogShapefileData(shpFilePath);

            log.info("=== Shapefile 업로드 완료 ===");
            model.addAttribute("message", "파일 업로드가 완료되었습니다!");
            model.addAttribute("success", true);

        } catch (Exception e) {
            log.error("Shapefile 업로드 실패: {}", e.getMessage(), e);
            model.addAttribute("message", "파일 업로드 실패: " + e.getMessage());
            model.addAttribute("success", false);
        }

        return "admin/upload";
    }

    private void validateShapefiles(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("모든 파일을 업로드해주세요.");
            }
        }
    }

    private void logFileInfo(MultipartFile file, String type) {
        log.info("{} 파일 정보:", type);
        log.info("  - 파일명: {}", file.getOriginalFilename());
        log.info("  - 크기: {} bytes ({} KB)", file.getSize(), file.getSize() / 1024);
        log.info("  - Content-Type: {}", file.getContentType());
    }

    private String saveTemporaryFiles(MultipartFile... files) throws IOException {
        String uploadDir = System.getProperty("java.io.tmpdir") + "/shapefile_upload/";
        Files.createDirectories(Paths.get(uploadDir));

        String shpFilePath = null;

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir + filename);
            file.transferTo(targetPath);
            log.info("임시 저장: {}", targetPath.toString());

            // SHP 파일 경로 저장
            if (filename.toLowerCase().endsWith(".shp")) {
                shpFilePath = targetPath.toString();
            }
        }

        return shpFilePath;
    }

    private void parseAndLogShapefileData(String shpFilePath) {
        try {
            File shpFile = new File(shpFilePath);

            // Shapefile DataStore 생성
            ShapefileDataStore dataStore = new ShapefileDataStore(shpFile.toURI().toURL());
            dataStore.setCharset(Charset.forName("EUC-KR")); // 한글 인코딩

            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureType schema = featureSource.getSchema();

            log.info("=== Shapefile 데이터 분석 ===");
            log.info("Feature Type: {}", schema.getTypeName());
            log.info("좌표계: {}", schema.getCoordinateReferenceSystem());
            log.info("속성 개수: {}", schema.getAttributeCount());

            // 속성 정보 로깅
            log.info("=== 속성 정보 ===");
            for (AttributeDescriptor attr : schema.getAttributeDescriptors()) {
                log.info("- {}: {} ({})",
                    attr.getLocalName(),
                    attr.getType().getBinding().getSimpleName(),
                    attr.getType().getName());
            }

            // 실제 데이터 읽기
            SimpleFeatureCollection features = featureSource.getFeatures();
            log.info("총 Feature 개수: {}", features.size());

            // 처음 5개 Feature 상세 정보
            try (SimpleFeatureIterator iterator = features.features()) {
                int count = 0;
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    log.info("=== Feature {} ===", count + 1);

                    // 모든 속성 출력
                    for (Property prop : feature.getProperties()) {
                        String name = prop.getName().getLocalPart();
                        Object value = prop.getValue();

                        if ("the_geom".equals(name) || "geom".equals(name)) {
                            // 기하 정보는 타입과 좌표 범위만
                            if (value instanceof Geometry) {
                                Geometry geom = (Geometry) value;
                                log.info("  {}: {} (좌표 개수: {})", name, geom.getGeometryType(), geom.getNumPoints());
                                log.info("    범위: {}", geom.getEnvelopeInternal());
                            }
                        } else {
                            log.info("  {}: {}", name, value);
                        }
                    }
                    count++;
                }
            }

            dataStore.dispose();
            log.info("=== Shapefile 분석 완료 ===");

        } catch (Exception e) {
            log.error("Shapefile 파싱 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Shapefile 파싱 실패", e);
        }
    }
}