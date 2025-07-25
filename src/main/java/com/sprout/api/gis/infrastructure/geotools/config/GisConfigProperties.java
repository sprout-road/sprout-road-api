package com.sprout.api.gis.infrastructure.geotools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gis")
public class GisConfigProperties {

    private Coordinate coordinate;

    @Data
    public static class Coordinate {
        private boolean forceXy;
        private String sourceEpsg;
        private String targetEpsg;
    }
}