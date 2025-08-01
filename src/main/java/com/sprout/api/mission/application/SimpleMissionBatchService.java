package com.sprout.api.mission.application;

import static com.sprout.api.mission.utils.MissionPromptTemplate.buildMissionPrompt;

import com.sprout.api.common.client.RegionClient;
import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.mission.application.dto.AiMissionResponse;
import com.sprout.api.mission.application.result.BatchResult;
import com.sprout.api.mission.infrastructure.GeminiTemplate;
import com.sprout.api.mission.infrastructure.SlackTemplate;
import com.sprout.api.mission.utils.AiResponseParser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleMissionBatchService {

    private final MissionService missionService;
    private final SlackTemplate slackTemplate;
    private final GeminiTemplate geminiTemplate;
    private final AiResponseParser aiResponseParser;
    private final RegionClient regionClient;

    @Scheduled(cron = "0 0 18 * * ?", zone = "Asia/Seoul")
    public void testSpecialCityBatch() {
        try {
            List<RegionInfoDto> allRegions = regionClient.getAllRegions();
            BatchResult result = new BatchResult(allRegions.size());;
            slackTemplate.sendBatchStart(allRegions.size());
            this.processRegions(allRegions, result);
            slackTemplate.sendBatchComplete(result);
        } catch (Exception e) {
            log.error("배치 작업 전체 실패", e);
            slackTemplate.sendBatchError(e.getMessage());
        }
    }

    private void processRegions(List<RegionInfoDto> regions, BatchResult result) throws InterruptedException {
        this.processRegion(regions.get(0), result);
        for (int i = 1; i < regions.size(); i++) {
            Thread.sleep(4000);
            this.processRegion(regions.get(i), result);
            this.sendProgressIfNeeded(i + 1, regions.size(), result);
        }
    }

    private void processRegion(RegionInfoDto region, BatchResult result) {
        try {
            String prompt = buildMissionPrompt(region.getRegionCode(), region.getRegionName());
            String aiResponse = geminiTemplate.generate(prompt);
            List<AiMissionResponse> missions = aiResponseParser.parseToMissions(aiResponse);

            missionService.saveMissions(region.getRegionCode(), missions);
            result.addSuccess(region.getRegionCode());
        } catch (Exception e) {
            result.addFail(region.getRegionName());
        }
    }

    private void sendProgressIfNeeded(int current, int total, BatchResult result) {
        double progress = (double) current / total * 100;

        if (progress == 25.0 || progress == 50.0 || progress == 75.0) {
            slackTemplate.sendBatchProgress(result);
        }
    }
}