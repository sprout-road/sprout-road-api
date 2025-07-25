package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.ShapefileParsingStrategy;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.SigunguDto;
import com.strout.api.gis.domain.ShapefileType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SigunguParsingStrategy implements ShapefileParsingStrategy {

    private final ShapefileParsingSupport parsingSupport;

    @Override
    public void parse(ShapefileUploadCommand command) {
        try {
            File tempShpFile = parsingSupport.createTemporaryShapefileSet(command);
            parseSigunguData(tempShpFile);
        } catch (Exception e) {
            log.error("시/군/구 Shapefile 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("시/군/구 Shapefile 파싱 실패", e);
        }
    }

    @Override
    public boolean supports(ShapefileType type) {
        return type == ShapefileType.SIGUNGU;
    }

    /**
     * 시/군/구 데이터 파싱 및 로깅
     */
    private void parseSigunguData(File shpFile) {
        try {
            ShapefileDataStore dataStore = parsingSupport.createDataStore(shpFile);
            SimpleFeatureType schema = dataStore.getFeatureSource().getSchema();
            SimpleFeatureCollection features = dataStore.getFeatureSource().getFeatures();

            // 기본 스키마 정보 로깅
            parsingSupport.logBasicSchemaInfo(schema, features, "시/군/구");

            // 시/도별 그룹화 분석
            Map<String, List<SigunguDto>> provinceGroups = new HashMap<>();

            try (SimpleFeatureIterator iterator = features.features()) {
                int count = 0;
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    String sigCode = (String) feature.getAttribute("SIG_CD");
                    String sigKorName = (String) feature.getAttribute("SIG_KOR_NM");
                    String sigEngName = (String) feature.getAttribute("SIG_ENG_NM");

                    if (sigCode != null && sigCode.length() >= 2) {
                        String provinceCode = sigCode.substring(0, 2);
                        SigunguDto info = new SigunguDto(sigCode, sigKorName, sigEngName);

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
                    List<SigunguDto> districts = entry.getValue();

                    log.info("시/도 코드 {}: {}개 시/군/구", provinceCode, districts.size());

                    // 처음 3개만 표시
                    districts.stream().limit(3).forEach(info ->
                        log.info("  - {} ({})", info.korName(), info.code()));

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
}