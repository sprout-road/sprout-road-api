package com.sprout.api.travel.ui.request.dto;

import com.sprout.api.travel.application.command.ContentBlockCommand;
import com.sprout.api.travel.domain.vo.ContentType;
import java.util.Map;

public record ContentBlockDto(
    String type,
    Integer order,
    Map<String, String> content
) {

    public ContentBlockCommand toCommand() {
        ContentType contentType = ContentType.fromValue(type);
        return new ContentBlockCommand(contentType, order, content);
    }
}
