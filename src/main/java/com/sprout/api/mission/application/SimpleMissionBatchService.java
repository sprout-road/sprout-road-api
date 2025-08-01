package com.sprout.api.mission.application;

import static com.sprout.api.mission.utils.MissionPromptTemplate.buildMissionPrompt;

import com.sprout.api.common.client.RegionClient;
import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.mission.application.dto.AiMissionResponse;
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

    private final SlackTemplate slackTemplate;
    private final GeminiTemplate geminiTemplate;
    private final AiResponseParser aiResponseParser;
    private final RegionClient regionClient;

    @Scheduled(cron = "0 31 15 * * ?", zone = "Asia/Seoul")
    public void testSpecialCityBatch() {
        try {
            List<RegionInfoDto> allRegions = regionClient.getAllRegions();
            this.processRegions(allRegions);
        } catch (Exception e) {
            log.error("배치 작업 전체 실패", e);
        }
    }

    private void processRegions(List<RegionInfoDto> regions) throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            this.processRegion(regions.get(i));
            this.waitForRateLimit(i, regions.size());
        }
    }

    private void processRegion(RegionInfoDto region) {
        try {
            slackTemplate.sendSuccess("다양성 높임 테스트2\n");
            String prompt = buildMissionPrompt(region.getRegionCode(), region.getRegionName());
            String aiResponse = geminiTemplate.generate(prompt);
            List<AiMissionResponse> missions = aiResponseParser.parseToMissions(aiResponse);

            slackTemplate.sendSuccess(missions.toString() + " 미션 10개 파싱 완료");
        } catch (Exception e) {
            slackTemplate.sendFail(region.getRegionName() + " 미션 파싱 실패");
        }
    }

    private void waitForRateLimit(int currentIndex, int totalSize) throws InterruptedException {
        if (currentIndex < totalSize - 1) {
            Thread.sleep(4000);
        }
    }
}