package com.example.shooting_game;

import org.json.JSONException;
import org.json.JSONObject;

public class Achievement {
    private String title;
    private String description;
    private boolean isUnlocked;
    private int targetValue;
    private int currentProgress;

    public Achievement(String title, String description, int targetValue) {
        this.title = title;
        this.description = description;
        this.targetValue = targetValue;
        this.currentProgress = 0;
        this.isUnlocked = false;
    }

    public void updateProgress(int value) {
        if (isUnlocked) return;  // Skip if already unlocked
        currentProgress = Math.min(currentProgress + value, targetValue);
        if (currentProgress >= targetValue) {
            isUnlocked = true; // Mark as complete when the target value is reached
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    private boolean isDisplayed = false;

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }
}

