package com.sprout.api.travel.ui.docs.constants;

public class SwaggerExamples {

    public static final String TRAVEL_LOG_CREATE_EXAMPLE = """
        {
          "title": "부산 여행",
          "sigunguCode": "26110", 
          "traveledAt": "2025-07-30",
          "contents": [
            {
              "type": "text",
              "order": 1,
              "content": {
                "text": "오늘 부산 여행이 정말 즐거웠어요!"
              }
            },
            {
              "type": "image",
              "order": 2,
              "content": {
                "url": "https://example.com/busan-beach.jpg",
                "caption": "해운대 해변의 멋진 풍경"
              }
            }
          ]
        }
        """;

    public static final String TRAVEL_LOG_UPDATE_EXAMPLE = """
        {
          "title": "부산 여행",
          "sigunguCode": "26110", 
          "traveledAt": "2025-07-30",
          "contents": [
            {
              "type": "image",
              "order": 1,
              "content": {
                "url": "https://example.com/busan-beach.jpg",
                "caption": "해운대 해변의 멋진 풍경"
              }
            },
            {
              "type": "text",
              "order": 2,
              "content": {
                "text": "오늘 부산 여행이 정말 즐거웠어요!"
              }
            }
          ]
        }
        """;

    public static final String TRAVEL_LOG_DETAIL_RESPONSE = """
        {
          "id": 1,
          "title": "부산 여행",
          "traveledAt": "2025-07-30",
          "contents": [
            {
              "type": "text",
              "order": 1,
              "content": {
                "text": "오늘 부산 여행이 정말 즐거웠어요!"
              }
            },
            {
              "type": "image",
              "order": 2,
              "content": {
                "url": "https://example.com/busan-beach.jpg",
                "caption": "해운대 해변의 멋진 풍경"
              }
            }
          ]
        }
        """;
}