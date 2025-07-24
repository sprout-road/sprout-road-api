package com.sprout.api.gis.ui;

import com.sprout.api.gis.application.LocationService;
import com.sprout.api.gis.application.command.dto.LocationHighlightDto;
import com.sprout.api.gis.ui.dto.LocationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
public class LocationController {
    
    private final LocationService locationService;

    @PostMapping("/locate/highlight")
    public ResponseEntity<LocationHighlightDto> locateForHighlight(@RequestBody LocationRequest dto) {
        log.info("🗺️ 위치 하이라이트 요청: lat={}, lng={}", dto.lat(), dto.lng());
        try {
            LocationHighlightDto highlight = locationService.findLocationForHighlight(dto.lat(), dto.lng());
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