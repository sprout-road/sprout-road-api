package com.sprout.api.travel.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import com.sprout.api.common.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Table(name = "travel_logs")
public class TravelLog extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String regionCode;
    private String title;
    private LocalDate traveledAt;

    @Builder.Default
    @OneToMany(mappedBy = "travelLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentBlock> contentBlocks = new ArrayList<>();

    public static TravelLog of(Long userId, String regionCode, String title, LocalDate traveledAt) {
        if (title == null || title.isEmpty()) {
            throw new BusinessException(400, "제목은 필수");
        }
        if (title.length() > 10) {
            throw new BusinessException(400, "제목 길이 10자 제한");
        }
        return TravelLog.builder()
            .userId(userId)
            .regionCode(regionCode)
            .title(title)
            .traveledAt(traveledAt)
            .build();
    }

    public void addContentBlocks(List<ContentBlock> blocks) {
        this.contentBlocks.addAll(blocks);
        blocks.forEach(block -> block.setTravelLog(this));
    }

    public void updateContent(String title, List<ContentBlock> newBlocks) {
        this.title = title;
        this.contentBlocks.clear();

        newBlocks.forEach(block -> {
            block.setTravelLog(this);
            this.contentBlocks.add(block);
        });
    }
}