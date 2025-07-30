package com.sprout.api.gis.ui.docs;

import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "위치 및 지도 정보 API 문서", description = "/api/gis")
public interface LocationControllerDocs {

    @Operation(summary = "위도, 경도 정보로 위치 조회")
    ResponseEntity<LocationHighlightDto> locateForHighlight(Double lat, Double lng);
}
