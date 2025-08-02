package com.sprout.api.travel.domain;

import com.sprout.api.common.client.dto.TravelLogDto;
import com.sprout.api.travel.application.result.RegionLogResult;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TravelLogRepository extends JpaRepository<TravelLog, Long> {

    @Query("""
        select new com.sprout.api.travel.application.result.RegionLogResult(t.id, t.traveledAt, t.title)
        from TravelLog t
        where t.userId=:userId and t.regionCode=:sigunguCode
        order by t.id desc
    """)
    List<RegionLogResult> findAllByRegionCodeAndUserId(String regionCode, Long userId);

    @Query("SELECT new com.sprout.api.common.client.dto.TravelLogDto(t.id, t.title) " +
        "FROM TravelLog t " +
        "WHERE t.userId = :userId " +
        "AND t.traveledAt BETWEEN :fromDate AND :toDate")
    List<TravelLogDto> findTravelLogsByUserAndPeriod(Long userId, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT count(t.id) " +
        "FROM TravelLog t " +
        "WHERE t.userId = :userId " +
        "AND t.traveledAt BETWEEN :fromDate AND :toDate")
    Long countByPeriod(Long userId, LocalDate from, LocalDate to);
}
