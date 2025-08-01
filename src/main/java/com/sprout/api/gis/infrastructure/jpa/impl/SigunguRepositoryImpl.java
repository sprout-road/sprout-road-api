package com.sprout.api.gis.infrastructure.jpa.impl;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.gis.domain.Sigungu;
import com.sprout.api.gis.domain.SigunguRepository;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import com.sprout.api.gis.infrastructure.jpa.SigunguJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SigunguRepositoryImpl implements SigunguRepository {

    private final SigunguJpaRepository sigunguJpaRepository;

    @Override
    public long count() {
        return sigunguJpaRepository.count();
    }

    @Override
    public void saveAll(List<Sigungu> sigungus) {
        sigunguJpaRepository.saveAll(sigungus);
    }

    @Override
    public String findBySidoCodeAsGeoJson(String sidoCode) {
        return sigunguJpaRepository.findBySidoCodeAsGeoJsonUnified(sidoCode);
    }

    @Override
    public SigunguLocationInfo findByContainsPoint(double lng, double lat) {
        return sigunguJpaRepository.findByContainsPoint(lng, lat)
            .orElseThrow(() -> new BusinessException(404, "해당 위치의 지역 정보는 없음"));
    }


}
