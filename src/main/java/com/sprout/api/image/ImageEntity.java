package com.sprout.api.image;

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
	private Long referenceId;

	@Enumerated(EnumType.STRING)
	private ImageStatus status;

	protected ImageEntity(String imageKey, Long referenceId, ImagePurpose purpose) {
		validateBasicConstruction(imageKey, referenceId, purpose);
		this.imageKey = imageKey;
		this.referenceId = referenceId;
		this.purpose = purpose;
		this.status = ImageStatus.TEMPORARY;
	}

	public static ImageEntity create(String imageKey, Long referenceId, ImagePurpose purpose) {
		return new ImageEntity(imageKey, referenceId, purpose);
	}

	private static void validateBasicConstruction(String imageKey, Long referenceId, ImagePurpose purpose) {
		if (imageKey == null || imageKey.isBlank()) {
			throw new IllegalArgumentException();
		}
		if (referenceId == null) {
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
