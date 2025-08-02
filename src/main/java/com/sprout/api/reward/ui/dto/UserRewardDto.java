package com.sprout.api.reward.ui.dto;

import lombok.Data;

@Data
public class UserRewardDto {
    private String regionName;
    private String regionCode;
    private Integer rewardCount;

    public UserRewardDto(String regionName, String regionCode, Integer rewardCount) {
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.rewardCount = rewardCount != null ? rewardCount : 0;
    }
}