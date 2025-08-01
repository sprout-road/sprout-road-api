package com.sprout.api.mission.application;

import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.domain.UserMissionParticipation;
import com.sprout.api.mission.domain.UserMissionRepository;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMissionQueryService {

    private final UserMissionRepository userMissionRepository;
    private final Clock clock;

    public boolean existsTodayMission(String regionCode, Long userId) {
        LocalDate today = LocalDate.now(clock);
        return userMissionRepository.existsByUserIdAndRegionCodeAndMissionDate(userId, regionCode, today);
    }

    public void validateUserMission(String regionCode, Long userId, LocalDate missionDate) {
        if (userMissionRepository.existsByUserIdAndRegionCodeAndMissionDate(userId, regionCode, missionDate)) {
            throw new IllegalStateException("이미 시작된 미션입니다.");
        }
    }

    public UserDailyMissionResult getTodayMissions(Long userId, String regionCode) {
        LocalDate today = LocalDate.now(clock);

        UserMissionParticipation participation =
            userMissionRepository.findByUserIdAndRegionCodeAndMissionDate(userId, regionCode, today)
                .orElseThrow(() -> new IllegalStateException("시작된 미션이 없습니다."));

        return UserDailyMissionResult.from(participation);
    }
}
