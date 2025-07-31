package com.sprout.api.gis.application;

import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.domain.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public String getRegionBySidoCode(String sidoCode) {
        RegionType regionType = RegionType.determineRegionType(sidoCode);
        if (regionType == RegionType.PROVINCE) {
            return regionRepository.findSigunguRegionBySidoCode(sidoCode);
        }
        return regionRepository.findSidoRegion(sidoCode);
    }
}
