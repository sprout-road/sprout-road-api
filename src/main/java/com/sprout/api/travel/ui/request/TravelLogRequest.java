package com.sprout.api.travel.ui.request;

import com.sprout.api.travel.application.command.ContentBlockCommand;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.ui.request.dto.ContentBlock;
import java.time.LocalDateTime;
import java.util.List;

public record TravelLogRequest(
    String title,
    String sigunguCode,
    LocalDateTime traveledAt,
    List<ContentBlock> contents
) {

    public CreateTravelLogCommand toCommand(Long userId) {
        List<ContentBlockCommand> contentBlockCommands = contents.stream().map(ContentBlock::toCommand).toList();
        return new CreateTravelLogCommand(userId, title, sigunguCode, traveledAt, contentBlockCommands);
    }
}
