package com.sprout.api.common.client.dto;

import com.sprout.api.common.constants.ImagePurpose;
import java.io.InputStream;

public record ImageMetaData(
    InputStream inputStream,
    Long referenceId,
    ImagePurpose purpose,
    String originalFilename,
    String contentType,
    long contentSize
) {
}
