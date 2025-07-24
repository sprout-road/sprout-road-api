package com.sprout.api.config.gis;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gis")
public class GisConfigProperties {

    private Coordinate coordinate;
    private Map<String, String> systemProperties;

    @Data
    public static class Coordinate {
        private boolean forceXy;
        private String sourceEpsg;
        private String targetEpsg;
    }
}