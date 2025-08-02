package com.sprout.api.reward.application;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.reward.domain.Reward;
import com.sprout.api.reward.domain.RewardRepository;
import com.sprout.api.reward.domain.UserReward;
import com.sprout.api.reward.domain.UserRewardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final ImageManageClient imageManageClient;

    public String upload(FileMetaData fileMetaData, String regionName, String regionCode) {
        String imageUrl = imageManageClient.uploadImage(fileMetaData, ImagePurpose.REWARD);
        rewardRepository.findByRegionCodeAndRegionName(regionCode, regionName)
                .ifPresentOrElse(reward -> reward.updateImageUrl(imageUrl), () -> {
                    Reward reward = Reward.create(regionCode, regionName, imageUrl);
                    rewardRepository.save(reward);
                });
        imageManageClient.markImagesAsUsed(List.of(imageUrl));
        return imageUrl;
    }

    public String provide(String regionCode, Long userId) {
        Reward reward = rewardRepository.findByRegionCode(regionCode)
            .orElseThrow(() -> new BusinessException(404, "존재하지 않는 리워드 정보입니다."));

        userRewardRepository.findByRewardAndUserId(reward, userId)
            .ifPresentOrElse(UserReward::incrementCount, () -> {
                UserReward userReward = new UserReward(userId, reward);
                userReward.incrementCount();
                userRewardRepository.save(userReward);
            });

        return reward.getImageUrl();
    }
}
