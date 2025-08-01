package com.sprout.api.mission.application.result;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BatchResult {
    private final List<String> successRegions = new ArrayList<>();
    private final List<String> failRegions = new ArrayList<>();
    private final LocalDateTime startTime = LocalDateTime.now();
    private int totalExpected;

    public BatchResult(int totalExpected) {
        this.totalExpected = totalExpected;
    }

    public void addSuccess(String regionName) {
        successRegions.add(regionName);
    }

    public void addFail(String regionName) {
        failRegions.add(regionName);
    }

    public int getSuccessCount() {
        return successRegions.size();
    }

    public int getFailCount() {
        return failRegions.size();
    }

    public int getTotalProcessed() {
        return getSuccessCount() + getFailCount();
    }

    public double getSuccessRate() {
        if (getTotalProcessed() == 0) return 0.0;
        return (double) getSuccessCount() / getTotalProcessed() * 100;
    }

    public Duration getDuration() {
        return Duration.between(startTime, LocalDateTime.now());
    }
}