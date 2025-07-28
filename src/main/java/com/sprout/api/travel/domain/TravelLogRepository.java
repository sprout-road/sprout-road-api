package com.sprout.api.travel.domain;

import com.sprout.api.travel.application.result.RegionLogResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TravelLogRepository extends JpaRepository<TravelLog, Long> {

    @Query("""
        select new com.sprout.api.travel.application.result.RegionLogResult(t.traveledAt, t.title)
        from TravelLog t
        where t.userId=:userId and t.sigunguCode=:sigunguCode
        order by t.id desc
    """)
    List<RegionLogResult> findAllBySigunguCodeAndUserId(String sigunguCode, Long userId);
}
