package com.sprout.api.gis.ui;

import com.sprout.api.gis.application.LocationService;
import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import com.sprout.api.gis.application.result.LocationResult;
import com.sprout.api.gis.ui.docs.LocationControllerDocs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
public class LocationController implements LocationControllerDocs {
    
    private final LocationService locationService;

    @GetMapping("/locate/highlight")
    public ResponseEntity<LocationHighlightDto> locateForHighlight(
        @RequestParam Double lat,
        @RequestParam Double lng
    ) {
        log.info("🗺️ 위치 하이라이트 요청: lat={}, lng={}", lat, lng);
        LocationHighlightDto highlight = locationService.findLocationForHighlight(lat, lng);
        log.info("✅ 위치 분석 완료: {} - {}", highlight.targetName(), highlight.reason());
        return ResponseEntity.ok(highlight);
    }

    @GetMapping("/v2/locate")
    public ResponseEntity<LocationResult> locateForHighlightV2(
        @RequestParam Double lat,
        @RequestParam Double lng
    ) {
        log.info("🗺️ 위치 하이라이트 요청: lat={}, lng={}", lat, lng);
        LocationResult result = locationService.findLocation(lat, lng);
        log.info("✅ 위치 분석 완료: {} - {}", result.getRegionCode(), result.getRegionName());
        return ResponseEntity.ok(result);
    }
}