package com.sprout.api.gis.ui.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;

@Tag(name = "위치 및 지도 정보 API 문서", description = "/api/gis")
public interface MapsControllerDocs {

    @Operation(summary = "시도 지도 좌표 정보에 대한 cdn redirection")
    ResponseEntity<Void> getAllSido();

    @Operation(summary = "시도 별 시군구 지도 좌표 정보에 대한 cdn redirection")
    ResponseEntity<Void> getSigunguBySidoCode(@Pattern(regexp = "\\d{2}") String sidoCode);

    @Operation(summary = "시도 별 시군구 지도 경계 좌표 정보에 대한 cdn redirection")
    ResponseEntity<Void> getSidoBoundaries(@Pattern(regexp = "\\d{2}") String sidoCode);
}
