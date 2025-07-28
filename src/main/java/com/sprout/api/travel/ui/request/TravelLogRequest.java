package com.sprout.api.travel.ui.request;

import com.sprout.api.travel.ui.request.dto.ContentBlock;
import java.util.List;

public record TravelLogRequest(
    String title,
    String sigunguCode,
    List<ContentBlock> contents
) {
}
