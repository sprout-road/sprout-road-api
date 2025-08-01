package com.sprout.api.gis.infrastructure.jpa.impl;

import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.gis.application.result.LocationResult;
import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.infrastructure.jpa.SidoJpaRepository;
import com.sprout.api.gis.infrastructure.jpa.SigunguJpaRepository;
import com.sprout.api.gis.util.GisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RegionRepositoryImpl implements RegionRepository {

    private final SidoJpaRepository sidoJpaRepository;
    private final SigunguJpaRepository sigunguJpaRepository;

    @Override
    public String findSigunguRegionBySidoCode(String sidoCode) {
        return sigunguJpaRepository.findSigunguRegionBySidoCode(sidoCode);
    }

    @Override
    public String findSidoRegion(String sidoCode) {
        return sidoJpaRepository.findSidoRegionBySidoCode(sidoCode);
    }

    @Override
    public LocationResult findLocation(Double lat, Double lng) {
        return sidoJpaRepository.findSpecialCityPoint(lng, lat)
            .orElseGet(() -> {
                LocationResult locationResult = sigunguJpaRepository.findCityPoint(lng, lat)
                    .orElseThrow(() -> new IllegalArgumentException("no city point"));
                String cityName = locationResult.getRegionName();
                log.info("cityName: {}", cityName);
                String regionName = GisUtil.getRegionName(locationResult.getRegionCode().substring(0, 2));
                locationResult.setRegionName(regionName + " " + cityName);
                return locationResult;
            });
    }

    @Override
    public List<RegionInfoDto> findSpecialRegionNames() {
        return sidoJpaRepository.findAllSpecialCityNames();
    }

    @Override
    public List<RegionInfoDto> findNormalRegionNames() {
        return sigunguJpaRepository.findAllNormalCityNames();
    }
}
