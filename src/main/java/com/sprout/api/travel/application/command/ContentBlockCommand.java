package com.sprout.api.travel.application.command;

import com.sprout.api.travel.domain.vo.ContentType;
import com.sprout.api.travel.domain.vo.ContentValue;

public record ContentBlockCommand(
    ContentType type,
    Integer order,
    ContentValue content
) {
}