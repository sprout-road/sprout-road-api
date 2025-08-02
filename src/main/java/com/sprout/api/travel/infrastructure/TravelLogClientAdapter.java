package com.sprout.api.travel.infrastructure;

import com.sprout.api.common.client.TravelLogClient;
import com.sprout.api.common.client.dto.TravelLogDto;
import com.sprout.api.travel.domain.TravelLogRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TravelLogClientAdapter implements TravelLogClient {

    private final TravelLogRepository travelLogRepository;

    @Override
    public Long getTravelCountByPeriod(LocalDate from, LocalDate to, Long userId) {
        return travelLogRepository.countByPeriod(userId, from, to);
    }
}
