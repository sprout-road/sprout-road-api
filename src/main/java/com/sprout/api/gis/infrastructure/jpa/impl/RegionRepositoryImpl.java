package com.sprout.api.gis.infrastructure.jpa.impl;

import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.infrastructure.jpa.SidoJpaRepository;
import com.sprout.api.gis.infrastructure.jpa.SigunguJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
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

}
