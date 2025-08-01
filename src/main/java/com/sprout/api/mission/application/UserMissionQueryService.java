package com.sprout.api.mission.application;

import com.sprout.api.common.exception.BusinessException;
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
            throw new BusinessException(400, "이미 시작된 미션입니다.");
        }
    }

    public UserDailyMissionResult getTodayMissions(Long userId, String regionCode) {
        UserMissionParticipation todayParticipation = getTodayParticipation(userId, regionCode);
        return UserDailyMissionResult.from(todayParticipation);
    }

    public UserMissionParticipation getTodayParticipation(Long userId, String regionCode) {
        LocalDate today = LocalDate.now(clock);
        return userMissionRepository.findByUserIdAndRegionCodeAndMissionDate(userId, regionCode, today)
                .orElseThrow(() -> new BusinessException(404, "시작된 미션이 없습니다."));
    }
}
