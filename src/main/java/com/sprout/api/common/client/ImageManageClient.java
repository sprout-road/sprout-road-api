package com.sprout.api.common.client;

import com.sprout.api.common.client.dto.ImageMetaData;
import java.util.List;

public interface ImageManageClient {

    String uploadImage(ImageMetaData imageMetaData);
    void markImagesAsUsed(List<String> imageUrls);
    void markImagesAsUnused(List<String> imageUrls);
}
