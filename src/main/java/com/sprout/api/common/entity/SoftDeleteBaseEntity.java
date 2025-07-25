package com.sprout.api.common.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SoftDeleteBaseEntity {

	@Builder.Default
	@Column(nullable = false)
	private Boolean deleted = false;

	public void markDeleted() {
		this.deleted = true;
	}

	public void restore() {
		this.deleted = false;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public boolean isActive() {
		return !this.deleted;
	}
}