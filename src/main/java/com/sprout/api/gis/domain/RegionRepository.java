package com.sprout.api.gis.domain;

public interface RegionRepository {

    String findSigunguRegionBySidoCode(String sidoCode);

    String findSidoRegion(String sidoCode);
}
