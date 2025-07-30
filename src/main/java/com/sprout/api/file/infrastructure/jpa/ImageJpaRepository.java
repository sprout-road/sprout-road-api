package com.sprout.api.file.infrastructure.jpa;

import com.sprout.api.file.domain.ImageEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, String> {
    @Query("SELECT i.imageKey FROM ImageEntity i " +
        "WHERE i.status = 'TEMPORARY' " +
        "AND i.createdDate < :cutoffDate")
    List<String> findTemporaryImageKeysOlderThan(LocalDateTime cutoffDate);

    @Query("SELECT i.imageKey FROM ImageEntity i " +
        "WHERE i.status = 'UNUSED' " +
        "AND i.modifiedDate < :cutoffDate")
    List<String> findUnusedImageKeysOlderThan(LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM ImageEntity i WHERE i.imageKey = :imageKey")
    void deleteByImageKey(String imageKey);
}
