package com.sprout.api.travel.ui.request.dto;

import com.sprout.api.travel.application.command.ContentBlockCommand;
import com.sprout.api.travel.domain.vo.ContentType;
import com.sprout.api.travel.domain.vo.ImageContent;
import com.sprout.api.travel.domain.vo.TextContent;
import java.util.Map;

public record ContentBlockDto(
    String type,
    Integer order,
    Map<String, String> content
) {

    public ContentBlockCommand toCommand() {
        ContentType contentType = ContentType.fromValue(type);
        if (contentType == ContentType.IMAGE) {
            ImageContent imageContent = new ImageContent(content().get("url"), content.get("caption"));
            return new ContentBlockCommand(contentType, order, imageContent);
        }
        TextContent textContent = new TextContent(content().get("text"));
        return new ContentBlockCommand(contentType, order, textContent);
    }
}
