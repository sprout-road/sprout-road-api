package com.sprout.api.travel.application;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import com.sprout.api.travel.application.command.UpdateTravelLogCommand;
import com.sprout.api.travel.domain.ContentBlock;
import com.sprout.api.travel.domain.TravelLog;
import com.sprout.api.travel.domain.TravelLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TravelLogService {

    private final ImageManageClient imageManageClient;
    private final TravelLogRepository travelLogRepository;

    public Long writeTravelLog(CreateTravelLogCommand command) {
        TravelLog travelLog = command.toTravelLog();
        List<ContentBlock> contentBlocks = command.toContentBlocks();
        travelLog.addContentBlocks(contentBlocks);

        TravelLog saved = travelLogRepository.save(travelLog);
        imageManageClient.markImagesAsUsed(extractImageUrls(contentBlocks));

        return saved.getId();
    }

    public void updateTravelLog(UpdateTravelLogCommand command) {
        travelLogRepository.findById(command.travelLogId())
            .ifPresentOrElse(travelLog -> {
                List<String> oldImageUrls = extractImageUrls(travelLog.getContentBlocks());
                List<ContentBlock> newContentBlocks = command.toContentBlocks();
                travelLog.updateContent(command.title(), newContentBlocks);

                imageManageClient.markImagesAsUnused(oldImageUrls);
                imageManageClient.markImagesAsUsed(extractImageUrls(newContentBlocks));
            }, IllegalArgumentException::new);
    }

    private List<String> extractImageUrls(List<ContentBlock> contentBlocks) {
        return contentBlocks.stream()
            .filter(ContentBlock::isImage)
            .map(ContentBlock::getImageUrl)
            .toList();
    }
}
