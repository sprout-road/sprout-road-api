package com.sprout.api.common.utils;

import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.exception.BusinessException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class FileMetaDataExtractor {
    
    public FileMetaData extractFrom(MultipartFile file) {
        validateImageFile(file);

        return FileMetaData.of(
            () -> {
                try {
                    return file.getInputStream();
                } catch (IOException e) {
                    log.error("파일 스트림 생성 실패", e);
                    throw new BusinessException(500, "서버에서 파일 추출 중 버그 발생");
                }
            },
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );
    }
    
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "파일이 비어있습니다.");
        }
        ObjectValidator.validateNotBlank(file.getContentType(), "파일의 콘텐츠 타입은 필수입니다.");
    }
}