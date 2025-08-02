package com.sprout.api.reward.domain;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRewardRepository extends CrudRepository<UserReward, Long> {
    Optional<UserReward> findByRewardAndUserId(Reward reward, Long userId);
}
