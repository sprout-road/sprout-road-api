package com.sprout.api.mission.domain;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMissionRepository extends JpaRepository<UserMissionParticipation, Long> {

    boolean existsByUserIdAndRegionCodeAndMissionDate(Long userId, String regionCode, LocalDate missionDate);
}
