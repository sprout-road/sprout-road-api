package com.sprout.api.gis.util;

public class GisUtil {

    public static String getRegionName(String sidoCode) {
        return switch (sidoCode) {
            case "11" -> "서울특별시";
            case "26" -> "부산광역시";
            case "27" -> "대구광역시";
            case "28" -> "인천광역시";
            case "29" -> "광주광역시";
            case "30" -> "대전광역시";
            case "31" -> "울산광역시";
            case "36" -> "세종특별자치시";
            case "41" -> "경기도";
            case "42" -> "강원특별자치도";
            case "43" -> "충청북도";
            case "44" -> "충청남도";
            case "45" -> "전북특별자치도";
            case "46" -> "전라남도";
            case "47" -> "경상북도";
            case "48" -> "경상남도";
            case "50" -> "제주특별자치도";
            default -> "알 수 없는 지역";
        };
    }
}
