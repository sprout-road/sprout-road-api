package com.sprout.api.gis.domain;

public enum RegionType {
    SPECIAL_CITY,       // 특별시
    METROPOLITAN_CITY,  // 광역시
    PROVINCE;            // 도

    public static RegionType determineRegionType(String sidoCode) {
        return switch (sidoCode) {
            case "11" -> RegionType.SPECIAL_CITY;        // 서울특별시
            case "26" -> RegionType.METROPOLITAN_CITY;   // 부산광역시
            case "27" -> RegionType.METROPOLITAN_CITY;   // 대구광역시
            case "28" -> RegionType.METROPOLITAN_CITY;   // 인천광역시
            case "29" -> RegionType.METROPOLITAN_CITY;   // 광주광역시
            case "30" -> RegionType.METROPOLITAN_CITY;   // 대전광역시
            case "31" -> RegionType.METROPOLITAN_CITY;   // 울산광역시
            case "36" -> RegionType.SPECIAL_CITY;        // 세종특별자치시
            default -> RegionType.PROVINCE;              // 나머지는 도
        };
    }
}