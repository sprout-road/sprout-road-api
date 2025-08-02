package com.sprout.api.reward.domain;

import com.sprout.api.reward.ui.dto.UserRewardDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    Optional<Reward> findByRegionCodeAndRegionName(String regionCode, String regionName);

    Optional<Reward> findByRegionCode(String regionCode);

    @Query("SELECT new com.sprout.api.reward.ui.dto.UserRewardDto(r.regionName, r.regionCode, ur.count) " +
        "FROM Reward r LEFT JOIN UserReward ur ON r = ur.reward AND ur.userId = :userId " +
        "ORDER BY r.regionName")
    Page<UserRewardDto> findUserRewardsByUserIdOrderByRegionName(@Param("userId") Long userId, Pageable pageable);
}
