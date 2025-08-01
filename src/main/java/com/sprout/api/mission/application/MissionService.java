package com.sprout.api.mission.application;

import com.sprout.api.mission.application.dto.AiMissionResponse;
import com.sprout.api.mission.domain.Mission;
import com.sprout.api.mission.domain.MissionJpaRepository;
import com.sprout.api.mission.domain.MissionType;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {

    private final MissionJpaRepository missionJpaRepository;
    private final Clock clock;

    public void saveMissions(String regionCode, List<AiMissionResponse> aiMissions) {
        LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
        if (missionJpaRepository.existsByRegionCodeAndMissionDate(regionCode, tomorrow)) {
            log.info("미션이 이미 존재함 - 스킵: {} [{}]", regionCode, tomorrow);
            return;
        }
        List<Mission> missions = parse(regionCode, aiMissions, tomorrow);
        missionJpaRepository.saveAll(missions);
        log.info("미션 저장 완료: {} - {}개", regionCode, missions.size());
    }

    private List<Mission> parse(String regionCode, List<AiMissionResponse> aiMissions, LocalDate now) {
        List<Mission> missions = new ArrayList<>();
        for (int i = 0; i < aiMissions.size(); i++) {
            AiMissionResponse aiMissionResponse = aiMissions.get(i);
            MissionType missionType = MissionType.of(aiMissionResponse.getType());
            Mission mission = Mission.create(regionCode, now, i, missionType, aiMissionResponse.getDescription());
            missions.add(mission);
        }
        return missions;
    }
}
