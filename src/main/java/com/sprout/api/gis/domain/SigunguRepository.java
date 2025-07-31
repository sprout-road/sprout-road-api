package com.sprout.api.gis.domain;

import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import java.util.List;

public interface SigunguRepository {

    long count();
    void saveAll(List<Sigungu> sigungus);
    String findBySidoCodeAsGeoJson(String sidoCode);
    SigunguLocationInfo findByContainsPoint(double lng, double lat);
}
