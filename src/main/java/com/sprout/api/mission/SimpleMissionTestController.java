package com.sprout.api.mission;

import com.slack.api.Slack;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.ButtonElement;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import com.sprout.api.mission.infrastructure.GeminiTemplate;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class SimpleMissionTestController {

    private final GeminiTemplate geminiTemplate;

    @GetMapping("/mission/{regionCode}/{regionName}")
    public ResponseEntity<String> testMissionGeneration(
        @PathVariable String regionCode,
        @PathVariable String regionName) {

        try {
            String prompt = buildMissionPrompt(regionCode, regionName);

            log.info("미션 생성 테스트 시작 - 지역: {} ({})", regionName, regionCode);
            log.info("전송할 프롬프트:\n{}", prompt);

            String response = geminiTemplate.generate(prompt);

            log.info("AI 응답 결과:\n{}", response);

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("미션 생성 실패", e);
            return ResponseEntity.status(500)
                .contentType(MediaType.TEXT_PLAIN)
                .body("오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/mission-danyang")
    public ResponseEntity<String> testDanyangMission() {
        return testMissionGeneration("43800", "단양군");
    }


    private String buildMissionPrompt(String regionCode, String regionName) {
        return String.format("""
            ## 기본 정보
            **현재 위치 정보**: %s (지역코드: %s)
            **페르소나**: 15세 청소년, 도심을 벗어나 새로운 지역 문화 체험 목적
            **이전 미션**: 없음
            
            ## 미션 요구사항
            - 총 5개의 미션 제공
            - 각 미션은 다음 중 하나의 방법으로 인증 가능:
              1. **사진 인증**: 미션 장소에서 방문하거나 체험하는 등 사진으로 기록물을 남김
              2. **글쓰기 인증**: 미션 장소에 다녀온 소감이나 후기 혹은 다른 장소에 가고 싶었던 기록들을 남기는 글쓰기
            
            ## 미션 설명 길이 제한
            - 각 미션 설명은 **15자 이상 40자 이하**로 작성
            - 구체적인 장소명과 활동을 포함
            - "~~에서 ~~하기" 형태로 명확하게 작성
            
            ## 응답 형식 (JSON)
            **중요: 코드 블록(```) 없이 순수한 JSON만 출력하세요!**
            **마크다운 형식 절대 사용 금지!**
            
            다음과 같은 형태로만 응답:
            
            [
              {
                "type": "picture",
                "description": "지역 대표 관광지에서 인증샷 찍기"
              },
              {
                "type": "writing",
                "description": "지역 특색 체험 후 소감 쓰기"
              },
              {
                "type": "picture", 
                "description": "지역 명소에서 풍경 사진 찍기"
              },
              {
                "type": "writing",
                "description": "지역 문화 체험 후 후기 작성하기"
              },
              {
                "type": "picture",
                "description": "지역 전통시장에서 특산품과 함께 사진 찍기"
              }
            ]
            
            ## 중요 규칙
            1. **마크다운 금지**: 코드 블록(```) 절대 사용하지 말고 순수한 JSON만 출력
            2. **JSON 형식 준수**: 다른 텍스트나 설명 없이 오직 JSON 배열만 출력
            3. **5개 고정**: 정확히 5개의 미션만 제공
            4. **type 필드**: "picture" 또는 "writing"만 사용
            5. **15세 적합성**: 청소년이 안전하게 할 수 있는 활동으로 구성
            6. **현재 위치 기반**: %s 지역의 실제 관광지, 문화시설, 체험 프로그램을 조사하여 구체적으로 추천
            7. **지역 특색 반영**: %s만의 고유한 문화, 관광지, 음식, 축제 등을 활용
            8. **글자 수 준수**: 각 미션 설명은 반드시 15자 이상 40자 이하
            9. **다양성**: 관광지, 문화시설, 음식, 자연경관, 역사유적 등 다양한 분야 포함
            10. **응답 시작**: 반드시 [ 로 시작해서 ] 로 끝나는 JSON 배열만 출력
            
            ## AI 수행 절차
            1. %s 지역의 대표적인 관광지, 문화시설, 체험 프로그램 조사
            2. 15세 청소년에게 적합하고 안전한 장소 선별
            3. 해당 지역만의 특색있는 장소와 활동 우선 선택
            4. 사진 인증과 글쓰기 인증을 적절히 분배 (picture 3개, writing 2개 권장)
            5. 각 미션 설명이 15-40자 범위인지 확인
            6. JSON 형태로 최종 출력
            """, regionName, regionCode, regionName, regionName, regionName);
    }
}