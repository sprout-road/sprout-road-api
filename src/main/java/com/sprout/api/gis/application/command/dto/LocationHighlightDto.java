package com.sprout.api.gis.application.command.dto;

import com.sprout.api.gis.domain.RegionType;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import com.sprout.api.gis.util.GisUtil;
import lombok.Builder;

@Builder
public record LocationHighlightDto(
    String highlightType,    // "sido" or "sigungu"
    String targetCode,       // 색칠할 영역의 코드
    String targetName,       // 색칠할 영역의 이름
    String parentSidoCode,   // 상위 시도 코드 (sigungu인 경우)
    String reason,           // 판단 근거 (디버깅용)
    double centerLat,
    double centerLng
) {

    public static LocationHighlightDto fromRegion(RegionType regionType, SigunguLocationInfo location) {
        return switch (regionType) {
            case METROPOLITAN_CITY, SPECIAL_CITY -> getCityHighlight(location);
            case PROVINCE -> getProvinceHighlight(location);
        };
    }

    private static LocationHighlightDto getProvinceHighlight(SigunguLocationInfo location) {
        return new LocationHighlightDto(
            "sigungu",
            location.getSigCode(),
            location.getSigNameKo(),
            location.getSidoCode(),
            "도 지역은 시군구 단위 색칠",
            location.getCenterLat(),
            location.getCenterLng()
        );
    }

    private static LocationHighlightDto getCityHighlight(SigunguLocationInfo location) {
        String sidoCode = location.getSidoCode();
        return new LocationHighlightDto(
            "sido",
            sidoCode,
            GisUtil.getRegionName(sidoCode),
            sidoCode,
            "광역시/특별시는 전체 영역 색칠",
            location.getCenterLat(),
            location.getCenterLng()
        );
    }
}