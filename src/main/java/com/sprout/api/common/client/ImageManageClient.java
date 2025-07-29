package com.sprout.api.common.client;

import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import java.util.List;

public interface ImageManageClient {

    String uploadImage(FileMetaData fileMetaData, ImagePurpose purpose);
    void markImagesAsUsed(List<String> imageUrls);
    void markImagesAsUnused(List<String> imageUrls);
}
