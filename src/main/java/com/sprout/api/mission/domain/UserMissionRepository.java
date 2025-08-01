package com.sprout.api.mission.domain;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMissionRepository extends JpaRepository<UserMissionParticipation, Long> {

    boolean existsByUserIdAndRegionCodeAndMissionDate(Long userId, String regionCode, LocalDate missionDate);

    Optional<UserMissionParticipation> findByUserIdAndRegionCodeAndMissionDate(Long userId, String regionCode,
                                                                               LocalDate today);
}
