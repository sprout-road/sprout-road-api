package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.ShapefileParsingStrategy;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SidoParsingStrategy implements ShapefileParsingStrategy {

    private final ShapefileParsingSupport parsingSupport;

    @Override
    public void parse(ShapefileUploadCommand command) {
        try {
            File tempShpFile = parsingSupport.createTemporaryShapefileSet(command);
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
     * 시/도 데이터 파싱 및 로깅
     */
    private void parseSidoData(File shpFile) {
        try {
            ShapefileDataStore dataStore = parsingSupport.createDataStore(shpFile);
            SimpleFeatureType schema = dataStore.getFeatureSource().getSchema();
            SimpleFeatureCollection features = dataStore.getFeatureSource().getFeatures();

            // 기본 스키마 정보 로깅
            parsingSupport.logBasicSchemaInfo(schema, features, "시/도");

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
            throw new RuntimeException("시/도 Shapefile 파싱 실패", e);
        }
    }
}