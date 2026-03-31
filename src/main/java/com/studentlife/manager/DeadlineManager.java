package com.studentlife.manager;

import com.studentlife.model.Assignment;
import com.studentlife.storage.DataStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DeadlineManager {
    private List<Assignment> assignments;
    private final DataStorage storage;

    public DeadlineManager(DataStorage storage) {
        this.storage = storage;
        this.assignments = storage.loadAssignments();
    }

    public void addAssignment(Assignment a) {
        assignments.add(a);
        save();
    }

    public void removeAssignment(String id) {
        assignments.removeIf(a -> a.getId().equals(id));
        save();
    }

    public void toggleComplete(String id) {
        assignments.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .ifPresent(a -> a.setCompleted(!a.isCompleted()));
        save();
    }

    public List<Assignment> getAllAssignments() {
        return new ArrayList<>(assignments);
    }

    public List<Assignment> getPendingAssignments() {
        return assignments.stream()
                .filter(a -> !a.isCompleted())
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Assignment> getUpcomingAssignments(int count) {
        return assignments.stream()
                .filter(a -> !a.isCompleted())
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Assignment> getOverdueAssignments() {
        return assignments.stream()
                .filter(Assignment::isOverdue)
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .collect(Collectors.toList());
    }

    public List<Assignment> getDueSoonAssignments() {
        return assignments.stream()
                .filter(Assignment::isDueSoon)
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .collect(Collectors.toList());
    }

    public long countPending() {
        return assignments.stream().filter(a -> !a.isCompleted()).count();
    }

    public long countCompleted() {
        return assignments.stream().filter(Assignment::isCompleted).count();
    }

    public long countOverdue() {
        return assignments.stream().filter(Assignment::isOverdue).count();
    }

    private void save() {
        storage.saveAssignments(assignments);
    }
}
