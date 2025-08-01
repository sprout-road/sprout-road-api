package com.sprout.api.mission.application;

import com.sprout.api.common.client.ImageManageClient;
import com.sprout.api.common.client.RewardClient;
import com.sprout.api.mission.application.command.MissionSubmitCommand;
import com.sprout.api.mission.application.command.RefreshCommand;
import com.sprout.api.mission.application.result.UserDailyMissionResult;
import com.sprout.api.mission.domain.Mission;
import com.sprout.api.mission.domain.UserMissionDetail;
import com.sprout.api.mission.domain.UserMissionParticipation;
import com.sprout.api.mission.domain.UserMissionRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMissionService {

    private final MissionQueryService missionQueryService;
    private final UserMissionQueryService userMissionQueryService;
    private final RewardClient rewardClient;
    private final ImageManageClient imageManageClient;
    private final UserMissionRepository userMissionRepository;
    private final Clock clock;

    public UserDailyMissionResult startTodayMission(Long userId, String regionCode) {
        LocalDate today = LocalDate.now(clock);
        userMissionQueryService.validateUserMission(regionCode, userId, today);

        List<Mission> masterMissions = missionQueryService.getTodayMissionsByRegion(regionCode);
        UserMissionParticipation participation = UserMissionParticipation.create(userId, regionCode, today);
        for (int position = 0; position < 5; position++) {
            Mission mission = masterMissions.get(position);
            participation.addMission(
                UserMissionDetail.create(mission.getTypeValue(), mission.getDescription())
            );
        }

        UserMissionParticipation savedParticipation = userMissionRepository.save(participation);
        return UserDailyMissionResult.from(savedParticipation);
    }

    public UserDailyMissionResult refresh(RefreshCommand command) {
        UserMissionParticipation todayParticipation =
            userMissionQueryService.getTodayParticipation(command.userId(), command.regionCode());

        validateCanRefresh(todayParticipation);
        Mission newMission = getMission(command.regionCode(), todayParticipation.getAvailablePositions());
        UserMissionDetail targetMission = todayParticipation.getMission(command.missionId());
        targetMission.refresh(newMission.getTypeValue(), newMission.getDescription());
        todayParticipation.addShownPosition(newMission.getPosition());

        return UserDailyMissionResult.from(todayParticipation);
    }

    private static void validateCanRefresh(UserMissionParticipation todayParticipation) {
        if (!todayParticipation.canRefresh()) {
            throw new IllegalStateException("더 이상 새로고침할 수 없습니다.");
        }
    }

    private Mission getMission(String regionCode, List<Integer> availablePositions) {
        Integer newMissionPosition = availablePositions.get(new Random().nextInt(availablePositions.size()));
        return missionQueryService.getTodayMisisonByPosition(regionCode, newMissionPosition);
    }

    public String submitWriting(MissionSubmitCommand command) {
        UserMissionParticipation todayParticipation =
            userMissionQueryService.getTodayParticipation(command.userId(), command.regionCode());
        validateCanSubmission(todayParticipation);

        UserMissionDetail mission = todayParticipation.getMission(command.missionId());
        mission.submit(command.type(), command.submissionContent());
        processMissionSideEffect(mission);

        return rewardClient.getRegionReward(command.regionCode());
    }

    private void processMissionSideEffect(UserMissionDetail mission) {
        if (mission.isImageMission()) {
            imageManageClient.markImagesAsUsed(List.of(mission.getDescription()));
        }
    }

    private static void validateCanSubmission(UserMissionParticipation todayParticipation) {
        if (todayParticipation.getCompletedMissionCount() >= 5) {
            throw new IllegalStateException("오늘 모든 미션을 완료했습니다.");
        }
    }
}
