package com.strout.api.gis.infrastructure.geotools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoordinateConverter {

    private final MathTransform transformToWGS84;

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
}