package com.sprout.api.reward.ui.dto;

import lombok.Data;

@Data
public class UserRewardDto {
    private String regionName;
    private String regionCode;
    private Integer currentCount;

    public UserRewardDto(String regionName, String regionCode, Integer currentCount) {
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.currentCount = currentCount != null ? currentCount : 0;
    }
}