package com.sprout.api.travel.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    TEXT("text"), IMAGE("image");

    private final String value;

    public static ContentType fromValue(String value) {
        validateNotEmpty(value);
        String lowerValue = value.trim().toLowerCase();
        for (ContentType contentType : values()) {
            if (contentType.value.equals(lowerValue)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException();
    }

    private static void validateNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}
