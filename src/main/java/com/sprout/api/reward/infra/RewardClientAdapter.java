package com.sprout.api.reward.infra;

import com.sprout.api.common.client.RewardClient;
import org.springframework.stereotype.Component;

@Component
public class RewardClientAdapter implements RewardClient {
    @Override
    public String getRegionReward(String regionCode) {
        // todo
        return "reward url";
    }
}
