package com.sprout.api.gis.ui;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
@Slf4j
public class MapsController {

    @Value("${app.storage.cdn.uri}")
    private String cdnUri;

    @GetMapping("/sido")
    public ResponseEntity<Void> getAllSido() {
        log.info(" =========> sido ");
        String location = String.format("%s/maps/sido.json", cdnUri);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @GetMapping("/sigungu/{sidoCode}")
    public ResponseEntity<Void> getSigunguBySidoCode(@PathVariable @Pattern(regexp = "\\d{2}") String sidoCode) {
        String location = String.format("%s/maps/sigungu_%s.json", cdnUri, sidoCode);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }
}
