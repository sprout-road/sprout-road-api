package com.sprout.api.travel.ui.request.dto;

public record ContentBlock(
    String id,
    String type,
    Integer order,
    Object content
) {
}
