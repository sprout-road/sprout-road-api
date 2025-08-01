package com.sprout.api.mission.application;

import com.sprout.api.mission.domain.Mission;
import com.sprout.api.mission.domain.MissionJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MissionQueryService {

    private final MissionJpaRepository missionRepository;

    public List<Mission> queryAllMissions(String regionCode, LocalDate today) {
        return missionRepository.findByRegionCodeAndMissionDateOrderByPosition(regionCode, today);
    }
}
