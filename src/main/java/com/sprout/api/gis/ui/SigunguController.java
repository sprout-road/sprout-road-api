package com.sprout.api.gis.ui;

import com.sprout.api.gis.domain.SigunguRepository;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gis")
@Validated
public class SigunguController {

    private final SigunguRepository sigunguRepository;

    @GetMapping("/sigungu")
    public ResponseEntity<String> getAllSigungu() {
        String geoJson = sigunguRepository.findAllAsGeoJson();
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(geoJson);
    }

    @GetMapping("/sigungu/{sidoCode}")
    public ResponseEntity<String> getSigunguBySidoCode(
        @PathVariable
        @Pattern(regexp = "\\d{2}")
        String sidoCode
    ) {
        String geoJson = sigunguRepository.findBySidoCodeAsGeoJson(sidoCode);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(geoJson);
    }

    @GetMapping("/sigungu/count")
    public ResponseEntity<Long> getSigunguCount() {
        long count = sigunguRepository.count();
        return ResponseEntity.ok(count);
    }
}
