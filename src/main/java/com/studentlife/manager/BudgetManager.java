package com.studentlife.manager;

import com.studentlife.model.Category;
import com.studentlife.model.Transaction;
import com.studentlife.model.TransactionType;
import com.studentlife.storage.DataStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetManager {
    private List<Transaction> transactions;
    private final DataStorage storage;

    public BudgetManager(DataStorage storage) {
        this.storage = storage;
        this.transactions = storage.loadTransactions();
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        save();
    }

    public void removeTransaction(String id) {
        transactions.removeIf(t -> t.getId().equals(id));
        save();
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getRecentTransactions(int count) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public double getTotalBalance() {
        return transactions.stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getMonthlyExpenses() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(start))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getMonthlyIncome() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getDate().isBefore(start))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<Category, Double> getExpensesByCategory() {
        Map<Category, Double> map = new LinkedHashMap<>();
        for (Category c : Category.values()) map.put(c, 0.0);
        transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .forEach(t -> map.merge(t.getCategory(), t.getAmount(), Double::sum));
        return map;
    }

    private void save() {
        storage.saveTransactions(transactions);
    }
}
