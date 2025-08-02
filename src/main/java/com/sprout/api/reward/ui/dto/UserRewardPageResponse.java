package com.sprout.api.reward.ui.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRewardPageResponse {
    private int currentPage;
    private int totalPages;
    private List<UserRewardDto> rewards;
}