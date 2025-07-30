package com.sprout.api.file.service;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.file.domain.ImageEntity;
import com.sprout.api.file.infrastructure.jpa.ImageJpaRepository;
import com.sprout.api.file.infrastructure.storage.S3ImageUploader;
import com.sprout.api.file.util.MetaDataHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService implements ImageManageClient {

    private final S3ImageUploader s3ImageUploader;
    private final MetaDataHelper metaDataHelper;
    private final ImageJpaRepository imageJpaRepository;

    public String uploadImage(FileMetaData metaData, ImagePurpose purpose) {
        String originalFilename = metaData.originalFilename();
        String extension = metaDataHelper.extractExtension(originalFilename);
        String fileKey = s3ImageUploader.generateFileKey(purpose, extension);

        ImageEntity imageEntity = ImageEntity.create(fileKey, purpose);
        imageJpaRepository.save(imageEntity);

        return s3ImageUploader.uploadImage(fileKey, metaData);
    }

    public void markImagesAsUsed(List<String> imageUrls) {
        List<String> imageKeys = convertImageKeys(imageUrls);
        List<ImageEntity> images = imageJpaRepository.findAllById(imageKeys);
        for (ImageEntity imageEntity : images) {
            imageEntity.markAsUsed();
        }
    }

    public void markImagesAsUnused(List<String> imageUrls) {
        List<String> imageKeys = convertImageKeys(imageUrls);
        List<ImageEntity> images = imageJpaRepository.findAllById(imageKeys);
        for (ImageEntity imageEntity : images) {
            imageEntity.markAsUnused();
        }
    }

    private List<String> convertImageKeys(List<String> imageUrls) {
        return imageUrls.stream()
            .map(s3ImageUploader::extractImageKey)
            .toList();
    }
}
