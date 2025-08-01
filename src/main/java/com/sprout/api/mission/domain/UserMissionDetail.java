package com.sprout.api.mission.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_mission_details",
       indexes = {
           @Index(name = "idx_participation_position", columnList = "participationId, position"),
           @Index(name = "idx_participation_completed", columnList = "participationId, completed")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_participation_position",
                           columnNames = {"participationId", "position"})
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMissionDetail extends TimeBaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer position;
    
    @Column(nullable = false, length = 20)
    private String type;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer refreshCount = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private UserMissionParticipation participation;

    public static UserMissionDetail create(int position, String type, String description) {
        return UserMissionDetail.builder()
            .position(position)
            .type(type)
            .description(description)
            .build();
    }

    public void refresh(String newType, String newDescription) {
        this.type = newType;
        this.description = newDescription;
        this.refreshCount++;
        this.completed = false;
    }
    
    public void complete() {
        this.completed = true;
    }

    public boolean match(Long id, int position) {
        return this.id.equals(id) && this.position.equals(position);
    }
}