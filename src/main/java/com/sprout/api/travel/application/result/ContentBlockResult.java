package com.sprout.api.travel.application.result;

import com.sprout.api.travel.domain.ContentBlock;
import java.util.Map;

public record ContentBlockResult(
    String type,
    Integer order,
    Map<String, String> content
) {

    public static ContentBlockResult of (ContentBlock contentBlock) {
        return new ContentBlockResult(
            contentBlock.getContentType().getValue(),
            contentBlock.getDisplayOrder(),
            contentBlock.getContent()
        );
    }
}
