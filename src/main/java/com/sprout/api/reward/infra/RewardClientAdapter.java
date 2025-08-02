package com.sprout.api.reward.infra;

import com.sprout.api.common.client.RewardClient;
import com.sprout.api.reward.application.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RewardClientAdapter implements RewardClient {

    private final RewardService rewardService;

    @Override
    public String provideReward(String regionCode, Long userId) {
        return rewardService.provide(regionCode, userId);
    }
}
