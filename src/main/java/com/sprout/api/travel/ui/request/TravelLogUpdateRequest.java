package com.sprout.api.travel.ui.request;

import com.sprout.api.travel.application.command.ContentBlockCommand;
import com.sprout.api.travel.application.command.UpdateTravelLogCommand;
import com.sprout.api.travel.ui.request.dto.ContentBlockDto;
import java.util.List;

public record TravelLogUpdateRequest(
    String title,
    List<ContentBlockDto> contents
) {

    public UpdateTravelLogCommand toCommand(Long travelLogId, Long userId) {
        List<ContentBlockCommand> contentBlockCommands = contents.stream().map(ContentBlockDto::toCommand).toList();
        return new UpdateTravelLogCommand(userId, travelLogId, title, contentBlockCommands);
    }
}
