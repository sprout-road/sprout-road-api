package com.sprout.api.travel.application;

import com.sprout.api.travel.application.command.CreateTravelLogCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelLogService {

    public void writeTravelLog(CreateTravelLogCommand command, Long userId) {
        // todo: 생성 로직
    }
}
