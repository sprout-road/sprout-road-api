package com.sprout.api.mission.ui;

import com.sprout.api.mission.application.UserMissionService;
import com.sprout.api.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final UserMissionService userMissionService;

    @GetMapping("/status/{regionCode}")
    public ResponseEntity<Boolean> getMissionStatus(@PathVariable String regionCode) {
        Long userId = 1L;
        boolean result = userMissionService.existsTodayMission(regionCode, userId);
        return ResponseEntity.ok(result);
    }
}
