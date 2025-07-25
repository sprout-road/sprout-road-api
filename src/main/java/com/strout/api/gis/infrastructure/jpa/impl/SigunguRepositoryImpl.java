package com.strout.api.gis.infrastructure.jpa.impl;

import com.strout.api.gis.domain.Sigungu;
import com.strout.api.gis.domain.SigunguRepository;
import com.strout.api.gis.infrastructure.jpa.SigunguJpaRepository;
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
    public String findAllAsGeoJson() {
        return sigunguJpaRepository.findAllAsGeoJson();
    }

    @Override
    public String findBySidoCodeAsGeoJson(String sidoCode) {
        return sigunguJpaRepository.findBySidoCodeAsGeoJson(sidoCode);
    }
}
