package com.sprout.api.mission.infrastructure.adapter;

import com.sprout.api.common.client.MissionClient;
import com.sprout.api.mission.domain.UserMissionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.geolatte.geom.M;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MissionClientAdapter implements MissionClient {

    private final UserMissionRepository userMissionRepository;

    @Override
    public Long getMissionCountByPeriod(LocalDate from, LocalDate to, Long userId, String regionCode) {
        return userMissionRepository.countByPeriod(userId, from, to, regionCode);
    }
}
