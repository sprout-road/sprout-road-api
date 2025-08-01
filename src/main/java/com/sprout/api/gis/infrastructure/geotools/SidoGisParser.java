package com.sprout.api.gis.infrastructure.geotools;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.gis.application.command.ShapefileUploadCommand;
import com.sprout.api.gis.domain.Sido;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SidoGisParser {

    private final ShapefileParsingSupport parsingSupport;
    private final CoordinateConverter coordinateConverter;

    public List<Sido> parse(ShapefileUploadCommand command) {
        try {
            File tempShpFile = parsingSupport.createTemporaryShapefileSet(command);
            return parseSidoData(tempShpFile);
        } catch (Exception e) {
            log.error("시/도 Shapefile 파싱 실패: {}", e.getMessage(), e);
            throw new BusinessException(500, "시/도 Shapefile 파싱 실패");
        }
    }

    /**
     * 시/도 데이터 파싱 및 Entity 생성
     */
    private List<Sido> parseSidoData(File shpFile) {
        List<Sido> results = new ArrayList<>();

        try {
            ShapefileDataStore dataStore = parsingSupport.createDataStore(shpFile);
            SimpleFeatureCollection features = dataStore.getFeatureSource().getFeatures();

            try (SimpleFeatureIterator iterator = features.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    String sidoCode = (String) feature.getAttribute("CTPRVN_CD");
                    String sidoKorName = (String) feature.getAttribute("CTP_KOR_NM");
                    String sidoEngName = (String) feature.getAttribute("CTP_ENG_NM");
                    Geometry originalGeom = (Geometry) feature.getAttribute("the_geom");

                    if (sidoCode != null && originalGeom != null) {
                        // 좌표 변환 (EPSG:5179 → EPSG:4326)
                        MultiPolygon wgs84Geom = coordinateConverter.convertToWGS84(originalGeom);

                        // Entity 생성
                        Sido sido = Sido.create(sidoCode, sidoKorName, sidoEngName, wgs84Geom);
                        results.add(sido);
                    }
                }
            }

            dataStore.dispose();
            log.info("시/도 파싱 완료: {}개", results.size());
            return results;
        } catch (Exception e) {
            log.error("시/도 Shapefile 파싱 오류: {}", e.getMessage(), e);
            throw new BusinessException(500, "시/도 Shapefile 파싱 실패");
        }
    }
}