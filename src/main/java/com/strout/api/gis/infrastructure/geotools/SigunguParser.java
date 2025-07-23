package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.Sigungu;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SigunguParser {

    private final ShapefileParsingSupport parsingSupport;
    private final CoordinateConverter coordinateConverter;

    public List<Sigungu> parse(ShapefileUploadCommand command) {
        try {
            File tempShpFile = parsingSupport.createTemporaryShapefileSet(command);
            return parseSigunguData(tempShpFile);
        } catch (Exception e) {
            log.error("시/군/구 Shapefile 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("시/군/구 Shapefile 파싱 실패", e);
        }
    }

    /**
     * 시/군/구 데이터 파싱 및 Entity 생성
     */
    private List<Sigungu> parseSigunguData(File shpFile) {
        List<Sigungu> results = new ArrayList<>();

        try {
            ShapefileDataStore dataStore = parsingSupport.createDataStore(shpFile);
            SimpleFeatureType schema = dataStore.getFeatureSource().getSchema();
            SimpleFeatureCollection features = dataStore.getFeatureSource().getFeatures();

            // 기본 스키마 정보 로깅
            parsingSupport.logBasicSchemaInfo(schema, features, "시/군/구");

            try (SimpleFeatureIterator iterator = features.features()) {
                int count = 0;
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    String sigCode = (String) feature.getAttribute("SIG_CD");
                    String sigKorName = (String) feature.getAttribute("SIG_KOR_NM");
                    String sigEngName = (String) feature.getAttribute("SIG_ENG_NM");
                    Geometry originalGeom = (Geometry) feature.getAttribute("the_geom");

                    if (sigCode != null && sigCode.length() >= 5 && originalGeom != null) {
                        // 좌표 변환 (EPSG:5179 → EPSG:4326)
                        MultiPolygon wgs84Geom = coordinateConverter.convertToWGS84(originalGeom);

                        // Entity 생성
                        Sigungu sigungu = Sigungu.create(sigCode, sigKorName, sigEngName, wgs84Geom);
                        results.add(sigungu);

                        // 처음 5개만 상세 로깅
                        if (count < 5) {
                            log.debug("시/군/구 파싱: {} ({}) - 좌표점 {}",
                                sigKorName, sigCode, originalGeom.getNumPoints());
                        }
                        count++;
                    }
                }
            }

            dataStore.dispose();
            log.info("시/군/구 파싱 완료: {}개", results.size());
            return results;

        } catch (Exception e) {
            log.error("시/군/구 Shapefile 파싱 오류: {}", e.getMessage(), e);
            throw new RuntimeException("시/군/구 Shapefile 파싱 실패", e);
        }
    }
}