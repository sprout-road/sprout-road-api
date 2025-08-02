package com.sprout.api.file.domain;

import com.sprout.api.common.constants.ImagePurpose;
import com.sprout.api.common.entity.TimeBaseEntity;
import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.common.utils.ObjectValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ImageEntity extends TimeBaseEntity {

	@Id
	@Column(name = "image_key")
	private String imageKey;

	@Enumerated(EnumType.STRING)
	@Check(constraints = "")
	private ImagePurpose purpose;

	@Enumerated(EnumType.STRING)
	@Check(constraints = "")
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
		ObjectValidator.validateNotBlank(imageKey, "Image key cannot be null or empty");
		ObjectValidator.validateNotNull(purpose, "Image purpose cannot be null");
	}

	public void markAsUsed() {
		if (isDeleted()) {
			throw new BusinessException(400, "삭제된 이미지의 상태를 변경할 수 없습니다");
		}
		this.status = ImageStatus.USED;
	}

	public void markAsUnused() {
		this.status = ImageStatus.UNUSED;
	}
}
