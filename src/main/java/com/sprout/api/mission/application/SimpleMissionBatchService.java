package com.sprout.api.mission.application;

import static com.sprout.api.mission.utils.MissionPromptTemplate.buildMissionPrompt;

import com.sprout.api.common.client.RegionClient;
import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.mission.infrastructure.GeminiTemplate;
import com.sprout.api.mission.infrastructure.SlackTemplate;
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
    private final RegionClient regionClient;

    @Scheduled(cron = "0 26 5 * * ?", zone = "Asia/Seoul")
    public void testSpecialCityBatch() {
        log.info("=== 특별시/광역시 미션 생성 테스트 시작 ===");

        try {
            List<RegionInfoDto> specialRegions = regionClient.getSpecialRegionNames();
            log.info("특별시/광역시 총 {}개 지역", specialRegions.size());

            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < specialRegions.size(); i++) {
                RegionInfoDto region = specialRegions.get(i);

                try {
                    log.info("미션 생성 시작: {} ({})", region.getRegionName(), region.getRegionCode());

                    // 1. 프롬프트 생성
                    String prompt = buildMissionPrompt(region.getRegionCode(), region.getRegionName());

                    // 2. AI 호출
                    String aiResponse = geminiTemplate.generate(prompt);

                    // 3. 응답 로깅 (나중에 파싱 로직 추가할 예정)
                    log.info("AI 응답 받음: {} - 길이: {}", region.getRegionName(), aiResponse.length());
                    log.debug("AI 응답 내용: {}", aiResponse);

                    slackTemplate.send("배치 + ai 임시 테스트 지역 - (" + region.getRegionName() +"): " + aiResponse);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("미션 생성 실패: {} ({})", region.getRegionName(), region.getRegionCode(), e);
                }

                // Rate Limit 고려 - 4초 대기 (분당 15회 제한)
                if (i < specialRegions.size() - 1) {
                    log.info("Rate Limit 대기 중... (4초)");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("배치 작업 중단됨");
                        break;
                    }
                }
            }

            log.info("=== 특별시/광역시 미션 생성 테스트 완료 === 성공: {}, 실패: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("배치 작업 전체 실패", e);
        }
    }
}