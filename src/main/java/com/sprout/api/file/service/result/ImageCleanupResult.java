package com.sprout.api.file.service.result;

import lombok.Getter;

@Getter
public class ImageCleanupResult {
    private int deletedCount = 0;
    private int failedCount = 0;
    
    public void incrementDeleted() {
        this.deletedCount++;
    }
    
    public void incrementFailed() {
        this.failedCount++;
    }
}