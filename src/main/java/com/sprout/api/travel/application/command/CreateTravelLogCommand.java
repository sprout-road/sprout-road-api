package com.sprout.api.travel.application.command;

import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.TravelLog;
import java.time.LocalDateTime;
import java.util.List;

public record CreateTravelLogCommand(
    Long userId,
    String title,
    String sigunguCode,
    LocalDateTime traveledAt,
    List<ContentBlockCommand> contents
) {

    public TravelLog toTravelLog() {
        return TravelLog.of(userId, sigunguCode, title, traveledAt);
    }

    public List<ContentBlock> toContentBlocks() {
        return contents.stream()
            .map(contentCommand -> ContentBlock.of(
                contentCommand.order(),
                contentCommand.content(),
                contentCommand.type()
            ))
            .toList();
    }
}