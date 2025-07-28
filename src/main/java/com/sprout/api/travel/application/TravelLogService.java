package com.sprout.api.travel.application;

import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.application.command.UpdateTravelLogCommand;
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
        TravelLog travelLog = command.toTravelLog();
        List<ContentBlock> contentBlocks = command.toContentBlocks();
        travelLog.addContentBlocks(contentBlocks);

        TravelLog saved = travelLogRepository.save(travelLog);
        return saved.getId();
    }

    public void updateTravelLog(UpdateTravelLogCommand command) {
        travelLogRepository.findById(command.travelLogId())
            .ifPresentOrElse(travelLog -> {
                List<ContentBlock> contentBlocks = command.toContentBlocks();
                travelLog.updateContent(command.title(), contentBlocks);
            }, IllegalAccessError::new);
    }
}
