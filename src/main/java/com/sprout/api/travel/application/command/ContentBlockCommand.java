package com.sprout.api.travel.application.command;

import com.sprout.api.travel.domain.vo.ContentType;
import java.util.Map;

public record ContentBlockCommand(
    ContentType type,
    Integer order,
    Map<String, String> content
) {
}