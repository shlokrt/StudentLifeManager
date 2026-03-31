package com.studentlife.model;

public enum Category {
    FOOD("🍔 Food"),
    STUDY("📚 Study"),
    TRANSPORT("🚌 Transport"),
    ENTERTAINMENT("🎬 Entertainment"),
    HEALTH("💊 Health"),
    BILLS("💡 Bills"),
    OTHER("📦 Other");

    private final String displayName;

    Category(String displayName) {
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
