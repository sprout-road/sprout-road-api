package com.sprout.api.gis.infrastructure.geotools.config;

import com.sprout.api.gis.infrastructure.geotools.config.GisConfigProperties.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(GisConfigProperties.class)
public class GisConfiguration {

    @Bean
    public MathTransform mathTransform(GisConfigProperties gisConfigProperties) {
        log.info("🗺️ GIS 설정 초기화 시작...");
        Coordinate coordinate = gisConfigProperties.getCoordinate();
        boolean forceXy = coordinate.isForceXy();
        String sourceEpsg = coordinate.getSourceEpsg();
        String targetEpsg = coordinate.getTargetEpsg();

        return getMathTransform(sourceEpsg, targetEpsg, forceXy);
    }

    // 좌표계 변환 빈 생성
    private MathTransform getMathTransform(String sourceEpsg, String targetEpsg, boolean forceXy) {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode(sourceEpsg, forceXy);
            CoordinateReferenceSystem targetCRS = CRS.decode(targetEpsg, forceXy);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, forceXy);
            log.info("좌표계 변환 빈 생성: {} → {}", sourceEpsg, targetEpsg);

            return transform;
        } catch (Exception e) {
            log.error("❌ GIS 설정 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GIS 설정 초기화 실패", e);
        }
    }
}