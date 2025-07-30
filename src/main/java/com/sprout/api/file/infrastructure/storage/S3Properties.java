package com.sprout.api.file.infrastructure.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class S3Properties {

	private final String bucket;
	private final String baseUrl;

	public S3Properties(
		@Value("${app.storage.cdn.bucket}") final String bucket,
		@Value("${app.storage.cdn.uri}") final String baseUrl
	) {
		this.bucket = bucket;
		this.baseUrl = baseUrl;
	}
}
