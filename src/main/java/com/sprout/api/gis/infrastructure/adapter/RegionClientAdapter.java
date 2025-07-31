package com.sprout.api.gis.infrastructure.adapter;

import com.sprout.api.common.client.RegionClient;
import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.util.GisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionClientAdapter implements RegionClient {

    private final RegionRepository regionRepository;

    @Override
    public List<RegionInfoDto> getSpecialRegionNames() {
        return regionRepository.findSpecialRegionNames();
    }

    @Override
    public List<RegionInfoDto> getNormalRegionNames() {
        List<RegionInfoDto> normalRegionNames = regionRepository.findNormalRegionNames();
        for (RegionInfoDto regionInfoDto : normalRegionNames) {
            String cityName = regionInfoDto.getRegionName();
            String regionName = GisUtil.getRegionName(regionInfoDto.getRegionCode().substring(0, 2));
            regionInfoDto.setRegionName(regionName + " " + cityName);
        }
        return normalRegionNames;
    }
}
