package com.sprout.api.gis.application;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import com.sprout.api.gis.application.result.LocationResult;
import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.domain.RegionType;
import com.sprout.api.gis.domain.SigunguRepository;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final SigunguRepository sigunguRepository;
    private final RegionRepository regionRepository;

    public LocationHighlightDto findLocationForHighlight(double lat, double lng) {
        validateCoordinates(lat, lng);
        log.info("위치 분석 시작: lat={}, lng={}", lat, lng);

        SigunguLocationInfo location = sigunguRepository.findByContainsPoint(lng, lat);
        String sidoCode = location.getSidoCode();
        RegionType regionType = RegionType.determineRegionType(sidoCode);
        log.info("발견된 위치: {} ({}), 시도코드: {}", location.getSigNameKo(), location.getSigCode(), sidoCode);

        return LocationHighlightDto.fromRegion(regionType, location);
    }

    private void validateCoordinates(double lat, double lng) {
        if (lat < 33.0 || lat > 39.0) {
            throw new BusinessException(400, "유효하지 않은 위도입니다. (33.0 ~ 39.0)");
        }

        if (lng < 124.0 || lng > 132.0) {
            throw new BusinessException(400, "유효하지 않은 경도입니다. (124.0 ~ 132.0)");
        }
    }

    public LocationResult findLocation(Double lat, Double lng) {
        return regionRepository.findLocation(lat, lng);
    }
}
