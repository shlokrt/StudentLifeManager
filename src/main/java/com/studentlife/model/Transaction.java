package com.studentlife.model;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private String id;
    private String description;
    private double amount;
    private Category category;
    private LocalDate date;
    private TransactionType type;

    public Transaction(String description, double amount, Category category,
                       LocalDate date, TransactionType type) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public TransactionType getType() { return type; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setType(TransactionType type) { this.type = type; }
}
