package com.sprout.api.common.utils;

import com.sprout.api.common.exception.BusinessException;

public class ObjectValidator {

    public static void validateNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(400, errorMessage);
        }
    }

    public static void validateNotNull(Object value, String errorMessage) {
        if (value == null) {
            throw new BusinessException(400, errorMessage);
        }
    }
}
