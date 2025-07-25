package com.sprout.api.gis.application.command.dto;

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
}