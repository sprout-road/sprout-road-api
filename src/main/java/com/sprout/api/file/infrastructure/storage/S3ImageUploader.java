package com.sprout.api.file.infrastructure.storage;

import com.sprout.api.common.client.dto.ImageMetaData;
import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.file.util.IdUtil;
import com.sprout.api.file.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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

	public String uploadImage(String imageKey, ImageMetaData metaData) {
		try {
			PutObjectRequest request = generatePutObject(imageKey, metaData.contentType(), metaData.contentSize());
			s3Client.putObject(request, RequestBody.fromInputStream(metaData.inputStream(), metaData.contentSize()));
			return getFullImageUrl(imageKey);
		} catch (S3Exception e) {
			log.error("S3 업로드 중 예외 발생: ", e);
			throw new IllegalArgumentException();
		}
	}

	public String getFullImageUrl(String imageKey) {
		return String.format("%s/%s", s3Properties.getBaseUrl(), imageKey);
	}

	public String extractImageKey(String imageUrl) {
		String baseUrl = s3Properties.getBaseUrl();
		String imageUrlPrefix = String.format("%s/%s", baseUrl, IMAGE_KEY_PREFIX);
		if (!imageUrl.startsWith(imageUrlPrefix)) {
			throw new IllegalArgumentException();
		}
		return imageUrl.substring(baseUrl.length() + 1);
	}

	public String generateFileKey(ImagePurpose purpose, String extension) {
		return String.format(FILE_KEY_FORMAT,
			IMAGE_KEY_PREFIX,
			purpose,
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
}
