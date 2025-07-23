package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.ShapefileParsingStrategy;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SidoParsingStrategy implements ShapefileParsingStrategy {

    @Override
    public void parse(ShapefileUploadCommand command) {
        try {
            File tempShpFile = createTemporaryShapefileSet(command);
            parseSidoData(tempShpFile);
        } catch (Exception e) {
            log.error("시/도 Shapefile 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("시/도 Shapefile 파싱 실패", e);
        }
    }

    @Override
    public boolean supports(ShapefileType type) {
        return type == ShapefileType.SIDO;
    }

    /**
     * Shapefile 세트 임시 파일 생성 (자동 삭제됨)
     */
    private File createTemporaryShapefileSet(ShapefileUploadCommand command) throws IOException {
        String uniqueId = "shapefile_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        File tempDir = Files.createTempDirectory("shapefile_upload").toFile();
        tempDir.deleteOnExit();

        File tempShpFile = new File(tempDir, uniqueId + ".shp");
        File tempDbfFile = new File(tempDir, uniqueId + ".dbf");
        File tempShxFile = new File(tempDir, uniqueId + ".shx");
        File tempPrjFile = new File(tempDir, uniqueId + ".prj");

        tempShpFile.deleteOnExit();
        tempDbfFile.deleteOnExit();
        tempShxFile.deleteOnExit();
        tempPrjFile.deleteOnExit();

        Files.write(tempShpFile.toPath(), command.shp().data(), StandardOpenOption.CREATE);
        Files.write(tempDbfFile.toPath(), command.dbf().data(), StandardOpenOption.CREATE);
        Files.write(tempShxFile.toPath(), command.shx().data(), StandardOpenOption.CREATE);
        Files.write(tempPrjFile.toPath(), command.prj().data(), StandardOpenOption.CREATE);

        log.info("임시 Shapefile 세트 생성 (자동 삭제 예정):");
        log.info("  - SHP: {}", tempShpFile.getAbsolutePath());
        log.info("  - DBF: {}", tempDbfFile.getAbsolutePath());
        log.info("  - SHX: {}", tempShxFile.getAbsolutePath());
        log.info("  - PRJ: {}", tempPrjFile.getAbsolutePath());

        return tempShpFile; // SHP 파일을 기준으로 반환
    }

    /**
     * 시/도 데이터 파싱 및 로깅
     */
    private void parseSidoData(File shpFile) {
        try {
            ShapefileDataStore dataStore = new ShapefileDataStore(shpFile.toURI().toURL());
            dataStore.setCharset(Charset.forName("EUC-KR"));

            SimpleFeatureSource featureSource = dataStore.getFeatureSource();
            SimpleFeatureType schema = featureSource.getSchema();

            log.info("=== 시/도 Shapefile 데이터 분석 ===");
            log.info("Feature Type: {}", schema.getTypeName());
            log.info("좌표계: {}", schema.getCoordinateReferenceSystem());
            log.info("속성 개수: {}", schema.getAttributeCount());

            // 속성 정보 로깅
            log.info("=== 시/도 속성 정보 ===");
            for (AttributeDescriptor attr : schema.getAttributeDescriptors()) {
                log.info("- {}: {} ({})",
                    attr.getLocalName(),
                    attr.getType().getBinding().getSimpleName(),
                    attr.getType().getName());
            }

            SimpleFeatureCollection features = featureSource.getFeatures();
            log.info("총 시/도 개수: {}", features.size());

            // 시/도 데이터 상세 분석
            Map<String, String> sidoMap = new HashMap<>();
            try (SimpleFeatureIterator iterator = features.features()) {
                int count = 0;
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    String sidoCode = (String) feature.getAttribute("CTPRVN_CD");
                    String sidoKorName = (String) feature.getAttribute("CTP_KOR_NM");
                    String sidoEngName = (String) feature.getAttribute("CTP_ENG_NM");

                    sidoMap.put(sidoCode, sidoKorName);

                    if (count < 5) {
                        log.info("=== 시/도 {} ===", count + 1);
                        log.info("  코드: {}", sidoCode);
                        log.info("  한글명: {}", sidoKorName);
                        log.info("  영문명: {}", sidoEngName);

                        Geometry geom = (Geometry) feature.getAttribute("the_geom");
                        if (geom != null) {
                            log.info("  기하정보: {} (좌표 개수: {})", geom.getGeometryType(), geom.getNumPoints());
                            log.info("  좌표 범위: {}", geom.getEnvelopeInternal());
                        }
                    }
                    count++;
                }
            }

            log.info("=== 시/도 코드 매핑 정보 ===");
            sidoMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> log.info("{}: {}", entry.getKey(), entry.getValue()));

            dataStore.dispose();
            log.info("=== 시/도 데이터 분석 완료 ===");
        } catch (Exception e) {
            log.error("시/도 Shapefile 파싱 오류: {}", e.getMessage(), e);
            throw new IllegalArgumentException("시/도 Shapefile 파싱 실패", e);
        }
    }
}
