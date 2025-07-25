package com.sprout.api.gis.domain;

import java.util.List;

public interface SigunguRepository {

    long count();
    void saveAll(List<Sigungu> sigungus);
    String findAllAsGeoJson();
    String findBySidoCodeAsGeoJson(String sidoCode);
}
