package com.sprout.api.mission.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.mission.utils.JsonUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class UserMissionParticipation extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String regionCode;

    @Column(nullable = false)
    private LocalDate missionDate;

    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserMissionDetail> missions = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String shownMissionPositions = "[0,1,2,3,4]";

    public static UserMissionParticipation create(Long userId, String regionCode, LocalDate missionDate) {
        return UserMissionParticipation.builder()
            .userId(userId)
            .regionCode(regionCode)
            .missionDate(missionDate)
            .build();
    }

    public void addMission(UserMissionDetail mission) {
        missions.add(mission);
        mission.setParticipation(this);
    }

    public int getTotalRefreshCount() {
        return missions.stream()
            .mapToInt(UserMissionDetail::getRefreshCount)
            .sum();
    }

    public boolean canRefresh() {
        return getTotalRefreshCount() < 5;
    }

    public int getRemainingRefreshCount() {
        return 5 - getTotalRefreshCount();
    }

    public int getCompletedMissionCount() {
        return (int) missions.stream()
            .filter(UserMissionDetail::getCompleted)
            .count();
    }

    public List<Integer> getAvailablePositions() {
        List<Integer> shown = JsonUtils.parseStringToIntList(shownMissionPositions);
        return IntStream.rangeClosed(5, 9)
            .filter(pos -> !shown.contains(pos))
            .boxed()
            .toList();
    }

    public void addShownPosition(Integer newPosition) {
        List<Integer> shown = JsonUtils.parseStringToIntList(shownMissionPositions);
        shown.add(newPosition);
        this.shownMissionPositions = JsonUtils.convertIntListToString(shown);
    }

    public UserMissionDetail getMission(Long missionId) {
        return getMissions().stream()
            .filter(m -> m.match(missionId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(404, "해당 미션을 찾을 수 없습니다."));
    }
}
