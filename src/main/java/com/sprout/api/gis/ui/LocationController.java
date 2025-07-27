package com.sprout.api.gis.ui;

import com.sprout.api.gis.application.LocationService;
import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import com.sprout.api.gis.ui.dto.LocationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
public class LocationController {
    
    private final LocationService locationService;

    @GetMapping("/locate/highlight")
    public ResponseEntity<LocationHighlightDto> locateForHighlight(
        @RequestParam Double lat,
        @RequestParam Double lng
    ) {
        log.info("🗺️ 위치 하이라이트 요청: lat={}, lng={}", lat, lng);
        // todo: 나중에 리팩토링 할 예정 우선은 기능 구현 먼저 할 것
        try {
            LocationHighlightDto highlight = locationService.findLocationForHighlight(lat, lng);
            log.info("✅ 위치 분석 완료: {} - {}", highlight.targetName(), highlight.reason());
            return ResponseEntity.ok(highlight);
        } catch (IllegalArgumentException e) {
            log.warn("❌ 잘못된 좌표: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ 위치 분석 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}