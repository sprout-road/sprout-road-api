package com.sprout.api.reward.ui.dto;

import lombok.Data;

@Data
public class UserRewardDto {
    private String regionName;
    private String regionCode;
    private Integer rewardCount;
    private String imageUrl;

    public UserRewardDto(String regionName, String regionCode, Integer rewardCount, String imageUrl) {
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.rewardCount = rewardCount != null ? rewardCount : 0;
        this.imageUrl = imageUrl;
    }
}