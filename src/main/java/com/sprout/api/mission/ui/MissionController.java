package com.sprout.api.mission.ui;

import com.sprout.api.mission.application.UserMissionQueryService;
import com.sprout.api.mission.application.UserMissionService;
import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.ui.docs.MissionControllerDocs;
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
public class MissionController implements MissionControllerDocs {

    private final UserMissionQueryService userMissionQueryService;
    private final UserMissionService userMissionService;

    @GetMapping("{regionCode}/status")
    public ResponseEntity<Boolean> getMissionStatus(@PathVariable String regionCode) {
        Long userId = 1L;
        boolean result = userMissionQueryService.existsTodayMission(regionCode, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("{regionCode}/start")
    public ResponseEntity<UserDailyMissionResult> startMission(@PathVariable String regionCode) {
        Long userId = 1L; // 임시 사용자
        UserDailyMissionResult result = userMissionService.startTodayMission(userId, regionCode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{regionCode}")
    public ResponseEntity<UserDailyMissionResult> getMissions(@PathVariable String regionCode) {
        Long userId = 1L; // 임시 사용자
        UserDailyMissionResult result = userMissionQueryService.getTodayMissions(userId, regionCode);
        return ResponseEntity.ok(result);
    }
}
