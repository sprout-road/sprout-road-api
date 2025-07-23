package com.strout.api.gis.infrastructure.geotools;

import lombok.extern.slf4j.Slf4j;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoordinateConverter {

    private MathTransform transformToWGS84;

    public CoordinateConverter() {
        try {
            // PCS_ITRF2000_TM (EPSG:5179) → WGS84 (EPSG:4326)
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:5179");
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
            transformToWGS84 = CRS.findMathTransform(sourceCRS, targetCRS, true);
            
            log.info("좌표 변환 시스템 초기화 완료: EPSG:5179 → EPSG:4326");
        } catch (Exception e) {
            log.error("좌표 변환 시스템 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("좌표 변환 시스템 초기화 실패", e);
        }
    }

    /**
     * PCS_ITRF2000_TM (EPSG:5179) → WGS84 (EPSG:4326) 변환
     */
    public MultiPolygon convertToWGS84(Geometry originalGeometry) {
        if (originalGeometry == null) {
            return null;
        }

        try {
            Geometry transformed = JTS.transform(originalGeometry, transformToWGS84);
            
            if (transformed instanceof MultiPolygon) {
                return (MultiPolygon) transformed;
            } else {
                log.warn("변환된 기하정보가 MultiPolygon이 아님: {}", transformed.getGeometryType());
                // Polygon을 MultiPolygon으로 변환
                return originalGeometry.getFactory().createMultiPolygon(
                    new org.locationtech.jts.geom.Polygon[]{(org.locationtech.jts.geom.Polygon) transformed}
                );
            }
            
        } catch (Exception e) {
            log.error("WGS84 변환 실패: {}", e.getMessage(), e);
            throw new RuntimeException("좌표 변환 실패", e);
        }
    }

    /**
     * 기하정보 유효성 검사
     */
    public boolean isValidGeometry(Geometry geometry) {
        return geometry != null && geometry.isValid() && !geometry.isEmpty();
    }
}