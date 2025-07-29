package com.sprout.api.file.domain;

import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ImageEntity extends TimeBaseEntity {

	@Id
	@Column(name = "image_key")
	private String imageKey;

	@Enumerated(EnumType.STRING)
	private ImagePurpose purpose;

	@Enumerated(EnumType.STRING)
	private ImageStatus status;

	protected ImageEntity(String imageKey, ImagePurpose purpose) {
		validateBasicConstruction(imageKey, purpose);
		this.imageKey = imageKey;
		this.purpose = purpose;
		this.status = ImageStatus.TEMPORARY;
	}

	public static ImageEntity create(String imageKey, ImagePurpose purpose) {
		return new ImageEntity(imageKey, purpose);
	}

	private static void validateBasicConstruction(String imageKey, ImagePurpose purpose) {
		if (imageKey == null || imageKey.isBlank()) {
			throw new IllegalArgumentException();
		}
		if (purpose == null) {
			throw new IllegalArgumentException();
		}
	}

	public void markAsUsed() {
		if (isDeleted()) {
			throw new IllegalArgumentException();
		}
		this.status = ImageStatus.USED;
	}

	public void markAsUnused() {
		this.status = ImageStatus.UNUSED;
	}
}
