package com.sprout.api.travel.application.result;

import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.TravelLog;
import java.time.LocalDate;
import java.util.List;

public record TravelDetailResult(
    Long id,
    String title,
    LocalDate traveledAt,
    List<ContentBlockResult> contents
) {

    public static TravelDetailResult of(TravelLog travelLog) {
        return new TravelDetailResult(
            travelLog.getId(),
            travelLog.getTitle(),
            travelLog.getTraveledAt(),
            toContentBlockList(travelLog.getContentBlocks())
        );
    }

    private static List<ContentBlockResult> toContentBlockList(List<ContentBlock> contentBlocks) {
        return contentBlocks.stream()
            .map(ContentBlockResult::of)
            .toList();
    }
}
