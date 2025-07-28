package com.sprout.api.travel.application.command;

import com.sprout.api.travel.domain.ContentBlock;
import java.util.List;

public record UpdateTravelLogCommand(
    Long userId,
    Long travelLogId,
    String title,
    List<ContentBlockCommand> contents
) {

    public List<ContentBlock> toContentBlocks() {
        return contents.stream()
            .map(contentCommand -> ContentBlock.of(contentCommand.order(), contentCommand.content()))
            .toList();
    }
}