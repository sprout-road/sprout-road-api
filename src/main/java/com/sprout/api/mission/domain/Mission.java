package com.sprout.api.mission.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.geolatte.geom.M;

@Entity
@Table(name = "missions",
    indexes = {
        @Index(name = "idx_mission_date", columnList = "missionDate")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_region_date_position",
            columnNames = {"regionCode", "missionDate", "position"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String regionCode;

    @Column(nullable = false)
    private LocalDate missionDate;

    @Column(nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    public static Mission create(
        String regionCode,
        LocalDate now,
        Integer position,
        MissionType type,
        String description
    ) {
        return Mission.builder()
            .regionCode(regionCode)
            .missionDate(now)
            .position(position)
            .type(type)
            .description(description)
            .build();
    }

    public String getTypeValue() {
        return type.getValue();
    }
}