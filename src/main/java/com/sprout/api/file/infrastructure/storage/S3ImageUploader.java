package com.sprout.api.file.infrastructure.storage;

import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.utils.ObjectValidator;
import com.sprout.api.file.util.IdUtil;
import com.sprout.api.file.util.TimeUtil;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageUploader {

	private static final String IMAGE_KEY_PREFIX = "images";
	private static final String FILE_KEY_FORMAT = "%s/%s/%s/%s/%s_%s%s";
	private static final String FILE_DATE_FORMAT = "yyyyMMdd";
	private static final int FILE_KEY_SUFFIX_LENGTH = 8;

	private final S3Properties s3Properties;
	private final S3Client s3Client;
	private final IdUtil idUtil;
	private final TimeUtil timeUtil;

	public String uploadImage(String imageKey, FileMetaData metaData) {
		try (InputStream inputStream = metaData.getInputStream()){
			PutObjectRequest request = generatePutObject(imageKey, metaData.contentType(), metaData.contentSize());
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, metaData.contentSize()));
			return getFullImageUrl(imageKey);
		} catch (S3Exception e) {
			throw new IllegalArgumentException("S3 업로드 중 예외 발생: ", e);
		} catch (IOException e) {
            throw new IllegalArgumentException("스트림 읽어 오는 중 예외 발생: ", e);
        }
    }

	public String getFullImageUrl(String imageKey) {
		ObjectValidator.validateNotBlank(imageKey, "Image key cannot be null or empty");
		return String.format("%s/%s", s3Properties.getBaseUrl(), imageKey);
	}

	public String extractImageKey(String imageUrl) {
		String baseUrl = s3Properties.getBaseUrl();
		String imageUrlPrefix = String.format("%s/%s", baseUrl, IMAGE_KEY_PREFIX);
		if (!imageUrl.startsWith(imageUrlPrefix)) {
			throw new IllegalArgumentException("s3 file key가 아님");
		}
		return imageUrl.substring(baseUrl.length() + 1);
	}

	public String generateFileKey(ImagePurpose purpose, String extension) {
		return String.format(FILE_KEY_FORMAT,
			IMAGE_KEY_PREFIX,
			purpose.getValue(),
			idUtil.generateUniqueId(FILE_KEY_SUFFIX_LENGTH).substring(0, 2),
			timeUtil.getFormattedDate(FILE_DATE_FORMAT),
			idUtil.generateUniqueId(FILE_KEY_SUFFIX_LENGTH),
			timeUtil.getCurrentTimeMillis(),
			extension
		);
	}

	private PutObjectRequest generatePutObject(String key, String contentType, long contentLength) {
		return PutObjectRequest.builder()
			.bucket(s3Properties.getBucket())
			.key(key)
			.contentType(contentType)
			.contentLength(contentLength)
			.build();
	}

	public void deleteImage(String imageKey) {
		try {
			DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
				.bucket(s3Properties.getBucket())
				.key(imageKey)
				.build();
			s3Client.deleteObject(deleteRequest);
		} catch (S3Exception e) {
			log.error("S3 파일 삭제 실패 - key: {}", imageKey, e);
			throw new IllegalStateException("S3 파일 삭제 실패: " + imageKey, e);
		}
	}
}
