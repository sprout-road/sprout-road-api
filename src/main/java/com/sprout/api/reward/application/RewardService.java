package com.sprout.api.reward.application;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.reward.domain.Reward;
import com.sprout.api.reward.domain.RewardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RewardService {

    private final RewardRepository rewardRepository;
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
}
