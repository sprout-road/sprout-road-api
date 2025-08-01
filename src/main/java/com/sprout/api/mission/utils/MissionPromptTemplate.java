package com.sprout.api.mission.utils;

import java.time.LocalDate;

public class MissionPromptTemplate {

    private static final String MISSION_GENERATION_PROMPT = """
    ## 기본 정보
    **현재 위치 정보**: %s
    **페르소나**: 15세 청소년, 도심을 벗어나 새로운 지역 문화 체험 목적
    **현재 날짜**: %s (계절별 추천 용도!)
    
    ## 미션 요구사항
    - 총 **10개의 미션 제공** (반드시 10개!)
    - 각 미션은 다음 중 하나의 방법으로 인증 가능:
      1. **사진 인증**: 미션 장소에서 방문하거나 체험하는 등 사진으로 기록물을 남김
      2. **글쓰기 인증**: 미션 장소에 다녀온 소감이나 후기 혹은 다른 장소에 가고 싶었던 기록들을 남기는 글쓰기
    
    ## 다양성 필수 규칙 ⭐
    1. **유명 관광지 제한**: 너무 유명한 TOP 5 관광지만 사용 금지 -> 임베딩된 데이터 중 유명한 것을 포함하되, 랜덤하게 적절히
    2. **숨겨진 명소 우선**: 현지인 추천 장소, 최근 핫플, 인스타 성지 등 포함
    3. **테마 다양화 필수**:
       - 문화/역사 (2-3개)
       - 음식/맛집 (2-3개)
       - 자연/공원 (2개)
       - 쇼핑/체험 (2개)
       - 야경/풍경 (1-2개)
    4. **지역구별 포함**: %s 내 다양한 동네/구역 골고루 선택
    5. **시간대 다양화**: 오전/오후/저녁 활동 골고루 분배
    
    ## 미션 설명 길이 제한
    - 각 미션 설명은 **15자 이상 40자 이하**로 작성
    - 구체적인 장소명과 활동을 포함
    - "~~에서 ~~하기" 형태로 명확하게 작성
    
    ## 응답 형식 (JSON)
    **중요: 파싱을 위해 코드 블록(```) 없이 순수한 JSON만 출력하세요!**
    **마크다운 형식 절대 사용 금지!**
    
    정확히 10개의 미션을 이 형태로 응답:
    [
      {"type": "picture", "description": "구체적인 미션 설명"},
      {"type": "writing", "description": "구체적인 미션 설명"},
      {"type": "picture", "description": "구체적인 미션 설명"},
      {"type": "writing", "description": "구체적인 미션 설명"},
      {"type": "picture", "description": "구체적인 미션 설명"},
      {"type": "writing", "description": "구체적인 미션 설명"},
      {"type": "picture", "description": "구체적인 미션 설명"},
      {"type": "writing", "description": "구체적인 미션 설명"},
      {"type": "picture", "description": "구체적인 미션 설명"},
      {"type": "writing", "description": "구체적인 미션 설명"}
    ]
    
    ## 중요 규칙
    1. **마크다운 금지**: 코드 블록(```) 절대 사용하지 말고 순수한 JSON만 출력
    2. **JSON 형식 준수**: 다른 텍스트나 설명 없이 오직 JSON 배열만 출력
    3. **10개 고정**: 정확히 10개의 미션만 제공 (5개 아님!)
    4. **type 필드**: "picture" 또는 "writing"만 사용 (picture 5개, writing 5개 권장)
    5. **15세 적합성**: 청소년이 안전하게 할 수 있는 활동으로 구성
    6. **현재 위치 기반**: %s 지역의 실제 관광지, 문화시설, 체험 프로그램을 조사하여 구체적으로 추천
    7. **지역 특색 반영**: %s만의 고유한 문화, 관광지, 음식, 축제 등을 활용
    8. **글자 수 준수**: 각 미션 설명은 반드시 15자 이상 40자 이하
    9. **다양성 필수**: 위의 다양성 규칙을 반드시 준수
    10. **응답 시작**: 반드시 [ 로 시작해서 ] 로 끝나는 JSON 배열만 출력
    
    ## AI 수행 절차
    1. %s 지역의 유명/비유명 관광지, 문화시설, 체험 프로그램 폭넓게 조사
    2. 유명한 TOP 3는 최대 2개 ~ 3개만 선택, 나머지는 적절히 선택
    3. 응답 Top3에 항상 유명한 Top3가 응답되는 문제가 있음 -> 적절히 응답순서를 분배 바람
    3. 13세~19세 청소년에게 적합하고 다양한 테마의 장소 선별
    4. 지역 내 다양한 동네/구역에서 골고루 선택
    5. 사진 인증과 글쓰기 인증을 5:5로 분배
    6. 각 미션 설명이 15-40자 범위인지 확인
    7. 다양성 규칙 준수 여부 최종 검토
    8. JSON 형태로 10개 미션 출력
    """;

    public static String buildMissionPrompt(String regionName) {
        LocalDate date = LocalDate.now();
        return String.format(MISSION_GENERATION_PROMPT,
            regionName, date, regionName, regionName, regionName, regionName);
    }
}