package com.sprout.api.mission.application;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.mission.domain.Mission;
import com.sprout.api.mission.domain.MissionJpaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MissionQueryService {

    private final Clock clock;
    private final MissionJpaRepository missionRepository;

    public List<Mission> getTodayMissionsByRegion(String regionCode) {
        LocalDate today = LocalDate.now(clock);
        return missionRepository.findByRegionCodeAndMissionDateOrderByPosition(regionCode, today);
    }

    public Mission getTodayMisisonByPosition(String regionCode, int position) {
        LocalDate today = LocalDate.now(clock);
        return missionRepository.findByRegionCodeAndMissionDateAndPosition(regionCode, today, position)
            .orElseThrow(() -> new BusinessException(404, "찾을 수 없는 미션 정보입니다."));
    }
}
