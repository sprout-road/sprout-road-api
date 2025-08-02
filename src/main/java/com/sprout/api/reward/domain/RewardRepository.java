package com.sprout.api.reward.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    Optional<Reward> findByRegionCodeAndRegionName(String regionCode, String regionName);

    Optional<Reward> findByRegionCode(String regionCode);
}
