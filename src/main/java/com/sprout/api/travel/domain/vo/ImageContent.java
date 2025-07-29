package com.sprout.api.travel.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageContent implements ContentValue {
    private String url;
    private String caption;
}