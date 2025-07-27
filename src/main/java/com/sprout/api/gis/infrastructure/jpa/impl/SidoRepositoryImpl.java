package com.sprout.api.gis.infrastructure.jpa.impl;

import com.sprout.api.gis.domain.Sido;
import com.sprout.api.gis.domain.SidoRepository;
import com.sprout.api.gis.infrastructure.jpa.SidoJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SidoRepositoryImpl implements SidoRepository {

    private final SidoJpaRepository jpaRepository;

    @Override
    public long count() {
        return jpaRepository.count();
    }

    public void saveAll(List<Sido> sidos) {
        jpaRepository.saveAll(sidos);
    }

    @Override
    public String findAllAsGeoJson() {
        return jpaRepository.findAllAsGeoJson();
    }

    @Override
    public String findSidoNameBySidoCode(String sidoCode) {
        return jpaRepository.findSidoNameBySidoCode(sidoCode)
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String findSidoBoundaries(String sidoCode) {
        return jpaRepository.findSidoBoundaries(sidoCode);
    }
}
