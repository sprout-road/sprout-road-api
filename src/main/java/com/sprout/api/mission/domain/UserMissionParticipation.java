package com.sprout.api.mission.domain;

import com.sprout.api.mission.utils.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "user_mission_participation",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_region_date",
                           columnNames = {"userId", "regionCode", "missionDate"})
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMissionParticipation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String regionCode;
    
    @Column(nullable = false)
    @CreatedDate
    private LocalDate missionDate;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer refreshCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String visibleMissions;
    
    @Column(columnDefinition = "TEXT")
    private String shownMissions;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ParticipationStatus status = ParticipationStatus.ACTIVE;

    public List<Integer> getVisibleMissionsList() {
        return JsonUtils.parseStringToIntList(this.visibleMissions);
    }
    
    public void setVisibleMissionsList(List<Integer> missions) {
        this.visibleMissions = JsonUtils.convertIntListToString(missions);
    }
    
    public List<Integer> getShownMissionsList() {
        return JsonUtils.parseStringToIntList(this.shownMissions);
    }
    
    public void setShownMissionsList(List<Integer> missions) {
        this.shownMissions = JsonUtils.convertIntListToString(missions);
    }

    public boolean canRefresh() {
        return this.refreshCount < 5;
    }

    public void useRefresh() {
        if (!canRefresh()) {
            throw new IllegalStateException("새로고침 횟수를 초과했습니다.");
        }
        this.refreshCount++;
    }
}