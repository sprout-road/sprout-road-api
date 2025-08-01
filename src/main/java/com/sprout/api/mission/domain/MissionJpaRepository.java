package com.sprout.api.mission.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionJpaRepository extends JpaRepository<Mission, Long> {
    boolean existsByRegionCodeAndMissionDate(String regionCode, LocalDate now);

    List<Mission> findByRegionCodeAndMissionDateOrderByPosition(String regionCode, LocalDate missionDate);

    Optional<Mission> findByRegionCodeAndMissionDateAndPosition(String regionCode, LocalDate today, int position);
}
