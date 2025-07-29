package com.sprout.api.file.service;

import com.sprout.api.file.infrastructure.jpa.ImageJpaRepository;
import com.sprout.api.file.infrastructure.storage.S3ImageUploader;
import com.sprout.api.file.service.result.ImageCleanupResult;
import com.sprout.api.file.service.result.ImageDeletionResult;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageCleanupService {

    private static final int TEMP_RETENTION_DAYS = 7;
    private static final int UNUSED_RETENTION_DAYS = 30;

    private final ImageJpaRepository imageJpaRepository;
    private final S3ImageUploader s3ImageUploader;
    private final Clock clock;

    @Scheduled(cron = "0 0 3 * * ?")
    public ImageCleanupResult cleanupUnusedImages() {
        log.info("이미지 정리 시작 - TEMP: {}일, UNUSED: {}일", TEMP_RETENTION_DAYS, UNUSED_RETENTION_DAYS);

        ImageCleanupResult result = new ImageCleanupResult();
        cleanupTemporaryImages(result);
        cleanupUnusedImages(result);

        log.info("이미지 정리 완료 - 삭제: {}개, 실패: {}개", result.getDeletedCount(), result.getFailedCount());
        return result;
    }

    private void cleanupTemporaryImages(ImageCleanupResult result) {
        LocalDateTime cutoffDate = LocalDateTime.now(clock).minusDays(TEMP_RETENTION_DAYS);

        List<String> imageKeys = imageJpaRepository.findTemporaryImageKeysOlderThan(cutoffDate);
        for (String imageKey : imageKeys) {
            ImageDeletionResult deletionResult = deleteImageSafely(imageKey);
            deletionResult.updateResult(result);
        }
    }

    private void cleanupUnusedImages(ImageCleanupResult result) {
        LocalDateTime cutoffDate = LocalDateTime.now(clock).minusDays(UNUSED_RETENTION_DAYS);

        List<String> imageKeys = imageJpaRepository.findUnusedImageKeysOlderThan(cutoffDate);
        for (String imageKey : imageKeys) {
            ImageDeletionResult deletionResult = deleteImageSafely(imageKey);
            deletionResult.updateResult(result);
        }
    }

    private ImageDeletionResult deleteImageSafely(String imageKey) {
        try {
            s3ImageUploader.deleteImage(imageKey);
            imageJpaRepository.deleteByImageKey(imageKey);
            return ImageDeletionResult.success();
        } catch (Exception e) {
            return ImageDeletionResult.failure();
        }
    }
}