package com.sprout.api.travel.application;

import com.sprout.api.travel.application.command.ContentBlockCommand;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.TravelLog;
import com.sprout.api.travel.domain.TravelLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelLogService {

    private final TravelLogRepository travelLogRepository;

    public Long writeTravelLog(CreateTravelLogCommand command) {
        TravelLog travelLog =
            TravelLog.of(command.userId(), command.sigunguCode(), command.title(), command.traveledAt());

        List<ContentBlock> contentBlocks = createContentBlocks(command.contents());
        travelLog.addContentBlocks(contentBlocks);

        TravelLog saved = travelLogRepository.save(travelLog);
        return saved.getId();
    }

    private static List<ContentBlock> createContentBlocks(List<ContentBlockCommand> contents) {
        return contents.stream()
            .map(contentCommand -> ContentBlock.of(
                contentCommand.id(),
                contentCommand.type(),
                contentCommand.order(),
                contentCommand.content()
            ))
            .toList();
    }
}
