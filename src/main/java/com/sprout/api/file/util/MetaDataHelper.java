package com.sprout.api.file.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MetaDataHelper {

    public void validateNotEmpty(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException();
        }
    }

    public String extractExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf(".");
        if (index <= 0 || index == originalFilename.length() - 1) {
            return "";
        }

        return originalFilename.substring(index).toLowerCase();
    }
}
