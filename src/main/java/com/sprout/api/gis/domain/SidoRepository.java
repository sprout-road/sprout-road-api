package com.sprout.api.gis.domain;

import java.util.List;

public interface SidoRepository {

    long count();

    void saveAll(List<Sido> sidos);

    String findAllAsGeoJson();

    String findSidoNameBySidoCode(String sidoCode);
}
