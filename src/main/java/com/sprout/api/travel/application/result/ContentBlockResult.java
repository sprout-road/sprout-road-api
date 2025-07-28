package com.sprout.api.travel.application.result;

import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.vo.ContentValue;

public record ContentBlockResult(
    String id,
    String type,
    int order,
    ContentValue content
) {

    public static ContentBlockResult of (ContentBlock contentBlock) {
        return new ContentBlockResult(
            contentBlock.getId(),
            contentBlock.getType().getValue(),
            contentBlock.getDisplayOrder(),
            contentBlock.getContent()
        );
    }
}
