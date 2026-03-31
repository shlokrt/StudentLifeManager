package com.studentlife.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Assignment {
    private String id;
    private String title;
    private String subject;
    private LocalDate dueDate;
    private TaskPriority priority;
    private boolean completed;
    private String description;

    public Assignment(String title, String subject, LocalDate dueDate,
                      TaskPriority priority, String description) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.priority = priority;
        this.description = description;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public LocalDate getDueDate() { return dueDate; }
    public TaskPriority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setDescription(String description) { this.description = description; }

    public boolean isOverdue() {
        return !completed && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public boolean isDueSoon() {
        if (completed || dueDate == null) return false;
        LocalDate now = LocalDate.now();
        return !dueDate.isBefore(now) && dueDate.isBefore(now.plusDays(4));
    }

    public long getDaysUntilDue() {
        if (dueDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
}
