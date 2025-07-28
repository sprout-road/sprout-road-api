package com.sprout.api.travel.application.result;

import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.vo.ContentValue;

public record ContentBlockResult(
    int order,
    ContentValue content
) {

    public static ContentBlockResult of (ContentBlock contentBlock) {
        return new ContentBlockResult(contentBlock.getDisplayOrder(), contentBlock.getContent());
    }
}
