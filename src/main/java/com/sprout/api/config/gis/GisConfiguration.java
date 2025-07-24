package com.sprout.api.config.gis;

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
    public MathTransform mathTransform(GisConfigProperties gisConfig) {
        log.info("🗺️ GIS 설정 초기화 시작...");

        try {
            // 시스템 프로퍼티 설정
            if (gisConfig.getSystemProperties() != null) {
                gisConfig.getSystemProperties().forEach((key, value) -> {
                    System.setProperty(key, value);
                    log.info("시스템 프로퍼티 설정: {}={}", key, value);
                });
            }

            // GIS 좌표 설정 로깅
            if (gisConfig.getCoordinate().isForceXy()) {
                log.info("GeoTools 좌표 순서: forceXY=true (경도, 위도 순서)");
            }

            // 좌표계 변환 빈 생성
            String sourceEpsg = gisConfig.getCoordinate().getSourceEpsg();
            String targetEpsg = gisConfig.getCoordinate().getTargetEpsg();

            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:5179");
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            log.info("좌표계 변환 빈 생성: {} → {}", sourceEpsg, targetEpsg);
            log.info("✅ GIS 설정 초기화 완료");

            return transform;

        } catch (Exception e) {
            log.error("❌ GIS 설정 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GIS 설정 초기화 실패", e);
        }
    }
}