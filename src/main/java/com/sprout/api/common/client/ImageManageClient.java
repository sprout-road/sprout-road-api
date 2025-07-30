package com.sprout.api.common.client;

import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import java.util.List;

public interface ImageManageClient {
    /**
      * 이미지를 업로드하고 URL을 반환합니다.
      * @param fileMetaData 업로드할 파일의 메타데이터
      * @param purpose 이미지 사용 목적
      * @return 업로드된 이미지의 URL
      * @throws IllegalArgumentException 파일 데이터가 유효하지 않은 경우
     */
    String uploadImage(FileMetaData fileMetaData, ImagePurpose purpose);

    /**
      * 지정된 이미지들을 사용됨으로 표시합니다.
      * @param imageUrls 사용됨으로 표시할 이미지 URL 목록
      */
    void markImagesAsUsed(List<String> imageUrls);

    /**
     * 지정된 이미지들을 사용되지 않음으로 표시합니다.
     * @param imageUrls 사용되지 않음으로 표시할 이미지 URL 목록
     */
    void markImagesAsUnused(List<String> imageUrls);
}
