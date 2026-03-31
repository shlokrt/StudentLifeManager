package com.studentlife.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.studentlife.model.Assignment;
import com.studentlife.model.Transaction;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private final Path dataDir;
    private final Path transactionsFile;
    private final Path assignmentsFile;
    private final Gson gson;

    public DataStorage() {
        dataDir = Paths.get(System.getProperty("user.home"), ".studentlifemanager");
        transactionsFile = dataDir.resolve("transactions.json");
        assignmentsFile = dataDir.resolve("assignments.json");

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, t, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, t, ctx) -> LocalDate.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    public void saveTransactions(List<Transaction> transactions) {
        saveToFile(transactionsFile, transactions);
    }

    public List<Transaction> loadTransactions() {
        Type type = new TypeToken<List<Transaction>>() {}.getType();
        List<Transaction> result = loadFromFile(transactionsFile, type);
        return result != null ? result : new ArrayList<>();
    }

    public void saveAssignments(List<Assignment> assignments) {
        saveToFile(assignmentsFile, assignments);
    }

    public List<Assignment> loadAssignments() {
        Type type = new TypeToken<List<Assignment>>() {}.getType();
        List<Assignment> result = loadFromFile(assignmentsFile, type);
        return result != null ? result : new ArrayList<>();
    }

    private void saveToFile(Path file, Object data) {
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private <T> T loadFromFile(Path file, Type type) {
        if (!Files.exists(file)) return null;
        try (Reader reader = Files.newBufferedReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading data: " + e.getMessage());
            return null;
        }
    }
}
