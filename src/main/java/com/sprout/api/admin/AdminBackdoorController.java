package com.sprout.api.admin;

import com.sprout.api.admin.docs.AdminBackdoorControllerDocs;
import com.sprout.api.mission.application.MissionService;
import com.sprout.api.mission.utils.DefaultMissionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/backdoor")
public class AdminBackdoorController implements AdminBackdoorControllerDocs {

    private final MissionService missionService;

    @PostMapping("/mission/{regionCode}")
    public void createMockMission(@PathVariable String regionCode) {
        missionService.saveTodayMissions(regionCode, DefaultMissionProvider.getDefaultMissions());
    }
}
