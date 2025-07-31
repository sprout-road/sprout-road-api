package com.sprout.api.gis.domain;

import com.sprout.api.gis.application.result.LocationResult;

public interface RegionRepository {

    String findSigunguRegionBySidoCode(String sidoCode);

    String findSidoRegion(String sidoCode);

    LocationResult findLocation(Double lat, Double lng);
}
