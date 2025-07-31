package com.sprout.api.mission.application;

import com.slack.api.Slack;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimpleMissionBatchService {
    
    @Scheduled(cron = "*/10 * * * * ?", zone = "Asia/Seoul")
    public void testBatch() {
        log.info("배치 테스트 실행: {}", LocalDateTime.now());
        // 일단 로그만 찍어서 스케줄러가 동작하는지 확인
    }
}