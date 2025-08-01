package com.sprout.api.file.util;

import com.sprout.api.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MetaDataHelper {

    public String extractExtension(String originalFilename) {
        validateNotEmpty(originalFilename);
        int index = originalFilename.lastIndexOf(".");
        if (index <= 0 || index == originalFilename.length() - 1) {
            return "";
        }

        return originalFilename.substring(index).toLowerCase();
    }


    private void validateNotEmpty(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException(400, "파일 이름이 없음. 잘못된 파일");
        }
    }
}
