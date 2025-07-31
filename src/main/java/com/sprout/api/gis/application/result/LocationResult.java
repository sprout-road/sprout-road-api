package com.sprout.api.gis.application.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationResult {
    private String regionCode;
    private String regionName;
    private double centerLat;
    private double centerLng;
}