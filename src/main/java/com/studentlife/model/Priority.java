package com.studentlife.model;

public enum Priority {
    HIGH("🔴 High"),
    MEDIUM("🟡 Medium"),
    LOW("🟢 Low");

    private final String displayName;

    Priority(String displayName) {
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
