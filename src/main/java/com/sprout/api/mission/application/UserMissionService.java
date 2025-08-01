package com.sprout.api.mission.application;

import com.sprout.api.mission.domain.UserMissionRepository;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMissionService {

    private final UserMissionRepository userMissionRepository;
    private final Clock clock;

    public boolean existsTodayMission(String regionCode, Long userId) {
        LocalDate today = LocalDate.now(clock);
        return userMissionRepository.existsByUserIdAndRegionCodeAndMissionDate(userId, regionCode, today);
    }
}
