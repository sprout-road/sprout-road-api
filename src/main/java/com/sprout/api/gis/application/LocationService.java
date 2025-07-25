package com.sprout.api.gis.application;

import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import com.sprout.api.gis.domain.RegionType;
import com.sprout.api.gis.domain.SidoRepository;
import com.sprout.api.gis.domain.SigunguRepository;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import com.sprout.api.gis.util.GisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final SigunguRepository sigunguRepository;
    private final SidoRepository sidoRepository;

    public LocationHighlightDto findLocationForHighlight(double lat, double lng) {
        validateCoordinates(lat, lng);
        log.info("위치 분석 시작: lat={}, lng={}", lat, lng);
        SigunguLocationInfo location = sigunguRepository.findByContainsPoint(lng, lat);
        String sidoCode = location.getSidoCode();

        log.info("발견된 위치: {} ({}), 시도코드: {}", location.getSigNameKo(), location.getSigCode(), sidoCode);
        // 2. 지역 타입 판단
        RegionType regionType = RegionType.determineRegionType(sidoCode);

        return switch (regionType) {
            case METROPOLITAN_CITY, SPECIAL_CITY -> {
                // 광역시/특별시: 전체 시도 영역을 색칠
                log.info("광역시/특별시 판정: {} - 전체 시도 색칠", GisUtil.getRegionName(sidoCode));
                yield LocationHighlightDto.builder()
                    .highlightType("sido")
                    .targetCode(sidoCode)
                    .targetName(sidoRepository.findSidoNameBySidoCode(sidoCode))
                    .centerLat(location.getCenterLat())
                    .centerLng(location.getCenterLng())
                    .reason("광역시/특별시는 전체 영역 색칠")
                    .build();
            }
            case PROVINCE -> {
                // 일반 도: 해당 시군구만 색칠
                log.info("일반 도 판정: {} - 시군구 단위 색칠", location.getSigNameKo());
                yield LocationHighlightDto.builder()
                    .highlightType("sigungu")
                    .targetCode(location.getSigCode())
                    .targetName(location.getSigNameKo())
                    .centerLat(location.getCenterLat())
                    .centerLng(location.getCenterLng())
                    .parentSidoCode(sidoCode)
                    .reason("도 지역은 시군구 단위 색칠")
                    .build();
            }
        };
    }

    private void validateCoordinates(double lat, double lng) {
        // 대한민국 대략적 범위 체크
        if (lat < 33.0 || lat > 39.0) {
            throw new IllegalArgumentException("유효하지 않은 위도입니다. (33.0 ~ 39.0)");
        }

        if (lng < 124.0 || lng > 132.0) {
            throw new IllegalArgumentException("유효하지 않은 경도입니다. (124.0 ~ 132.0)");
        }
    }
}
