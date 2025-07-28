package com.sprout.api.travel.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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
    private String sigunguCode;
    private String title;
    private LocalDateTime traveledAt;

    @OneToMany(mappedBy = "travelLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentBlock> contentBlocks = new ArrayList<>();

    public static TravelLog of(CreateTravelLogCommand command) {
        return TravelLog.builder()
            .userId(command.userId())
            .sigunguCode(command.sigunguCode())
            .title(command.title())
            .traveledAt(command.traveledAt())
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