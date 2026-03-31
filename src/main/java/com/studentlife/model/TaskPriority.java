package com.studentlife.model;

public enum TaskPriority {
    HIGH("🔴 High"),
    MEDIUM("🟡 Medium"),
    LOW("🟢 Low");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
