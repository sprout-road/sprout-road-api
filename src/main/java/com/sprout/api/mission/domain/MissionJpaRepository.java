package com.sprout.api.mission.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionJpaRepository extends JpaRepository<Mission, Long> {
    boolean existsByRegionCodeAndMissionDate(String regionCode, LocalDate now);

    List<Mission> findByRegionCodeAndMissionDateOrderByPosition(String regionCode, LocalDate missionDate);
}
