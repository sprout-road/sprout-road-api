package com.sprout.api.file.infrastructure.jpa;

import com.sprout.api.file.domain.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {
}
