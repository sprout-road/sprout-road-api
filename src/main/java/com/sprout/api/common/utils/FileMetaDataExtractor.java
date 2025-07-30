package com.sprout.api.common.utils;

import com.sprout.api.common.client.dto.FileMetaData;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileMetaDataExtractor {
    
    public FileMetaData extractFrom(MultipartFile file) {
        validateImageFile(file);

        return FileMetaData.of(
            () -> {
                try {
                    return file.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException("파일 스트림 생성 실패", e);
                }
            },
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );
    }
    
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        ObjectValidator.validateNotBlank(file.getContentType(), "파일의 콘텐츠 타입은 필수입니다.");
    }
}