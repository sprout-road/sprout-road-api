package com.strout.api.gis.infrastructure.jpa.impl;

import com.strout.api.gis.domain.Sido;
import com.strout.api.gis.domain.SidoRepository;
import com.strout.api.gis.infrastructure.jpa.SidoJpaRepository;
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
}
