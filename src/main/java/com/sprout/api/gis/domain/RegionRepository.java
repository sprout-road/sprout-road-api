package com.sprout.api.gis.domain;

import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.gis.application.result.LocationResult;
import java.util.List;

public interface RegionRepository {

    String findSigunguRegionBySidoCode(String sidoCode);

    String findSidoRegion(String sidoCode);

    LocationResult findLocation(Double lat, Double lng);

    List<RegionInfoDto> findSpecialRegionNames();

    List<RegionInfoDto> findNormalRegionNames();
}
