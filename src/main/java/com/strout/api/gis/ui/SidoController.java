package com.strout.api.gis.ui;

import com.strout.api.gis.domain.SidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
public class SidoController {

    private final SidoRepository sidoRepository;

    @GetMapping("/sido")
    public ResponseEntity<String> getAllSido() {
        String geoJson = sidoRepository.findAllAsGeoJson();
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(geoJson);
    }

    @GetMapping("/sido/count")
    public ResponseEntity<Long> getSidoCount() {
        long count = sidoRepository.count();
        return ResponseEntity.ok(count);
    }
}
