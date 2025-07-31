package com.sprout.api.admin;

import com.sprout.api.admin.docs.AdminV2ControllerDocs;
import com.sprout.api.gis.application.RegionService;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/v2")
public class AdminV2Controller implements AdminV2ControllerDocs {

    private final RegionService regionService;

    @GetMapping("/region/{sidoCode}")
    public ResponseEntity<byte[]> getRegion(@PathVariable String sidoCode) {
        String json = regionService.getRegionBySidoCode(sidoCode);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        String filename = "region_" + sidoCode + ".json";
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(bytes);
    }
}
