package com.sprout.api.reward.ui;

import com.sprout.api.reward.domain.RewardRepository;
import com.sprout.api.reward.ui.dto.UserRewardDto;
import com.sprout.api.reward.ui.dto.UserRewardPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/rewards")
@RequiredArgsConstructor
public class UserRewardApiController {

    private final RewardRepository rewardRepository;

    @GetMapping
    public ResponseEntity<UserRewardPageResponse> getUserRewards(
        @RequestParam(defaultValue = "0") int page
    ) {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(page, 12);
        Page<UserRewardDto> userRewards = rewardRepository.findUserRewardsByUserIdOrderByRegionName(userId, pageable);
        UserRewardPageResponse response =
            new UserRewardPageResponse(page, userRewards.getTotalPages(), userRewards.getContent());

        return ResponseEntity.ok(response);
    }
}