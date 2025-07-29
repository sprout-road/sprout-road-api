package com.sprout.api.common.client.dto;

import com.sprout.api.common.constants.ImagePurpose;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public record FileMetaData(
    InputStream inputStream,
    String originalFilename,
    String contentType,
    long contentSize
) {

    public static FileMetaData from(MultipartFile file) {
        try {
            return new FileMetaData(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }
}
