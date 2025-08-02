package com.sprout.api.mission.domain;

import com.sprout.api.mission.domain.dto.RegionMissionCountDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMissionRepository extends JpaRepository<UserMissionParticipation, Long> {

    boolean existsByUserIdAndRegionCodeAndMissionDate(Long userId, String regionCode, LocalDate missionDate);

    Optional<UserMissionParticipation> findByUserIdAndRegionCodeAndMissionDate(Long userId, String regionCode,
                                                                               LocalDate today);

    @Query("""
        SELECT new com.sprout.api.mission.domain.dto.RegionMissionCountDto(
            p.regionCode,
            COALESCE(SUM(CASE WHEN d.completed = true THEN 1L ELSE 0L END), 0L)
        )
        FROM UserMissionParticipation p
        LEFT JOIN p.missions d
        WHERE p.userId = :userId
        GROUP BY p.regionCode
        ORDER BY p.regionCode
        """)
    List<RegionMissionCountDto> findCompletedMissionCountByRegion(Long userId);

    @Query("SELECT d.id " +
        "FROM UserMissionParticipation p " +
        "JOIN p.missions d " +
        "WHERE p.userId = :userId " +
        "AND p.regionCode = :regionCode " +
        "AND p.missionDate BETWEEN :fromDate AND :toDate " +
        "AND d.completed = true")
    List<Long> findCompletedMissionIdsByUserAndPeriod(
        Long userId, LocalDate fromDate, LocalDate toDate, String regionCode
    );

    @Query("SELECT count(d.id) " +
        "FROM UserMissionParticipation p " +
        "JOIN p.missions d " +
        "WHERE p.userId = :userId " +
        "AND p.regionCode = :regionCode " +
        "AND p.missionDate BETWEEN :fromDate AND :toDate " +
        "AND d.completed = true")
    Long countByPeriod(Long userId, LocalDate fromDate, LocalDate toDate, String regionCode);
}
