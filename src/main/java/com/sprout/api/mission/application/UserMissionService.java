package com.sprout.api.mission.application;

import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.domain.Mission;
import com.sprout.api.mission.domain.UserMissionDetail;
import com.sprout.api.mission.domain.UserMissionParticipation;
import com.sprout.api.mission.domain.UserMissionRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMissionService {

    private final MissionQueryService missionQueryService;
    private final UserMissionQueryService userMissionQueryService;
    private final UserMissionRepository userMissionRepository;
    private final Clock clock;

    public UserDailyMissionResult startTodayMission(Long userId, String regionCode) {
        LocalDate today = LocalDate.now(clock);
        userMissionQueryService.validateUserMission(regionCode, userId, today);

        List<Mission> masterMissions = missionQueryService.queryAllMissions(regionCode, today);
        UserMissionParticipation participation = UserMissionParticipation.create(userId, regionCode, today);
        for (int position = 0; position < 5; position++) {
            Mission mission = masterMissions.get(position);
            participation.addMission(
                UserMissionDetail.create(position, mission.getTypeValue(), mission.getDescription())
            );
        }

        UserMissionParticipation savedParticipation = userMissionRepository.save(participation);
        return UserDailyMissionResult.from(savedParticipation);
    }
}
