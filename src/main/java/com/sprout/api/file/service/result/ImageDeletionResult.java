package com.sprout.api.file.service.result;

public class ImageDeletionResult {
    private final boolean success;
    
    private ImageDeletionResult(boolean success) {
        this.success = success;
    }
    
    public static ImageDeletionResult success() {
        return new ImageDeletionResult(true);
    }
    
    public static ImageDeletionResult failure() {
        return new ImageDeletionResult(false);
    }
    
    public void updateResult(ImageCleanupResult result) {
        if (success) {
            result.incrementDeleted();
            return;
        }
        result.incrementFailed();
    }
}