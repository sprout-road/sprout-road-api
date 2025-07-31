package com.sprout.api.common.client;

import com.sprout.api.common.client.dto.RegionInfoDto;
import java.util.List;

public interface RegionClient {

    List<RegionInfoDto> getSpecialRegionNames();

    List<RegionInfoDto> getNormalRegionNames();
}
