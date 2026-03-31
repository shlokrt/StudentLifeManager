package com.studentlife.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.studentlife.manager.BudgetManager;
import com.studentlife.manager.DeadlineManager;
import com.studentlife.model.Assignment;
import com.studentlife.model.Category;
import com.studentlife.model.TaskPriority;
import com.studentlife.model.Transaction;
import com.studentlife.model.TransactionType;
import com.studentlife.storage.DataStorage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainWindow {
    private final Stage stage;
    private final BudgetManager budgetManager;
    private final DeadlineManager deadlineManager;
    private final DataStorage storage;

    private StackPane contentArea;
    private Label navDashboard, navBudget, navDeadlines;

    private static final String SIDEBAR_BG    = "#1e293b";
    private static final String SIDEBAR_HOVER = "#334155";
    private static final String ACCENT        = "#6366f1";
    private static final String ACCENT_LIGHT  = "#818cf8";
    private static final String PAGE_BG       = "#f1f5f9";
    private static final String CARD_BG       = "#ffffff";
    private static final String TEXT_DARK     = "#0f172a";
    private static final String TEXT_MID      = "#475569";
    private static final String TEXT_LIGHT    = "#94a3b8";
    private static final String SUCCESS       = "#22c55e";
    private static final String DANGER        = "#ef4444";
    private static final String WARNING       = "#f59e0b";
    private static final String INCOME_CLR    = "#16a34a";
    private static final String EXPENSE_CLR   = "#dc2626";

    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.storage = new DataStorage();
        this.budgetManager = new BudgetManager(storage);
        this.deadlineManager = new DeadlineManager(storage);
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + PAGE_BG + ";");
        root.setCenter(contentArea);

        showDashboard();

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("Student Life Manager");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    // ─── Sidebar ────────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        // Logo area
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(28, 20, 24, 20));
        Label appIcon = new Label("🎓");
        appIcon.setFont(Font.font(32));
        Label appName = new Label("Student Life");
        appName.setFont(Font.font("System", FontWeight.BOLD, 18));
        appName.setTextFill(Color.WHITE);
        Label appSub = new Label("Manager");
        appSub.setFont(Font.font("System", FontWeight.BOLD, 18));
        appSub.setTextFill(Color.web(ACCENT_LIGHT));
        logoBox.getChildren().addAll(appIcon, appName, appSub);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #334155; -fx-border-color: transparent;");

        navDashboard  = buildNavItem("📊", "Dashboard", true);
        navBudget     = buildNavItem("💰", "Budget", false);
        navDeadlines  = buildNavItem("📅", "Deadlines", false);

        navDashboard.setOnMouseClicked(e  -> { activateNav(navDashboard);  showDashboard(); });
        navBudget.setOnMouseClicked(e     -> { activateNav(navBudget);     showBudget(); });
        navDeadlines.setOnMouseClicked(e  -> { activateNav(navDeadlines);  showDeadlines(); });

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label footer = new Label("v1.0 — BYOP Project");
        footer.setFont(Font.font(11));
        footer.setTextFill(Color.web(TEXT_LIGHT));
        footer.setPadding(new Insets(0, 0, 20, 20));

        sidebar.getChildren().addAll(logoBox, sep, navDashboard, navBudget, navDeadlines, spacer, footer);
        return sidebar;
    }

    private Label buildNavItem(String icon, String label, boolean active) {
        Label item = new Label("  " + icon + "  " + label);
        item.setFont(Font.font("System", FontWeight.NORMAL, 14));
        item.setMaxWidth(Double.MAX_VALUE);
        item.setPadding(new Insets(14, 20, 14, 20));
        item.setCursor(javafx.scene.Cursor.HAND);
        setNavStyle(item, active);

        item.setOnMouseEntered(e -> { if (!item.getStyle().contains(ACCENT)) setNavStyle(item, true); });
        item.setOnMouseExited(e  -> { if (item.getUserData() == null || !item.getUserData().equals("active")) setNavStyle(item, false); });
        return item;
    }

    private void setNavStyle(Label item, boolean active) {
        if (active) {
            item.setStyle("-fx-background-color: " + ACCENT + "; -fx-background-radius: 8;");
            item.setTextFill(Color.WHITE);
            item.setFont(Font.font("System", FontWeight.BOLD, 14));
        } else {
            item.setStyle("-fx-background-color: transparent;");
            item.setTextFill(Color.web("#cbd5e1"));
            item.setFont(Font.font("System", FontWeight.NORMAL, 14));
        }
    }

    private void activateNav(Label selected) {
        for (Label nav : new Label[]{navDashboard, navBudget, navDeadlines}) {
            nav.setUserData(null);
            setNavStyle(nav, false);
        }
        selected.setUserData("active");
        setNavStyle(selected, true);
    }

    // ─── Dashboard ──────────────────────────────────────────────────────────────

    private void showDashboard() {
        ScrollPane scroll = new ScrollPane(buildDashboardContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + PAGE_BG + "; -fx-background-color: " + PAGE_BG + "; -fx-border-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }

    private VBox buildDashboardContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + PAGE_BG + ";");

        // Header
        Label title = pageTitle("Dashboard");
        Label sub = pageSubtitle("Welcome back! Here's your overview.");

        // Alert banner
        long overdueCount = deadlineManager.countOverdue();
        long dueSoonCount = deadlineManager.getDueSoonAssignments().size();
        if (overdueCount > 0 || dueSoonCount > 0) {
            HBox alert = buildAlert(
                "⚠️  " + overdueCount + " overdue, " + dueSoonCount + " due within 3 days",
                "#fef9c3", "#854d0e");
            root.getChildren().addAll(title, sub, alert);
        } else {
            root.getChildren().addAll(title, sub);
        }

        // Stat cards row
        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("💰 Balance",   fmt(budgetManager.getTotalBalance()),   ACCENT,   budgetManager.getTotalBalance() >= 0 ? SUCCESS : DANGER),
            statCard("📈 Income",    fmt(budgetManager.getTotalIncome()),     "#0891b2", SUCCESS),
            statCard("📉 Expenses",  fmt(budgetManager.getTotalExpenses()),  "#dc2626", DANGER),
            statCard("📅 Pending",   deadlineManager.countPending() + " tasks", "#7c3aed", WARNING)
        );
        for (Node c : cards.getChildren()) HBox.setHgrow(c, javafx.scene.layout.Priority.ALWAYS);

        // Bottom row: recent transactions + upcoming deadlines
        HBox bottomRow = new HBox(16);
        bottomRow.getChildren().addAll(buildRecentTransactions(), buildUpcomingDeadlines());
        HBox.setHgrow(bottomRow.getChildren().get(0), javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(bottomRow.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);

        // Spending by category
        VBox spendChart = buildSpendingChart();

        root.getChildren().addAll(cards, bottomRow, spendChart);
        return root;
    }

    private HBox buildAlert(String msg, String bg, String fg) {
        HBox box = new HBox();
        box.setPadding(new Insets(12, 16, 12, 16));
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8;");
        Label lbl = new Label(msg);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        lbl.setTextFill(Color.web(fg));
        box.getChildren().add(lbl);
        return box;
    }

    private VBox statCard(String label, String value, String accentColor, String valueColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");
        card.setMinWidth(160);

        Rectangle accent = new Rectangle();
        accent.setWidth(36);
        accent.setHeight(4);
        accent.setArcWidth(4);
        accent.setArcHeight(4);
        accent.setFill(Color.web(accentColor));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", 12));
        lbl.setTextFill(Color.web(TEXT_MID));

        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 22));
        val.setTextFill(Color.web(valueColor));

        card.getChildren().addAll(accent, lbl, val);
        return card;
    }

    private VBox buildRecentTransactions() {
        VBox card = card("💳 Recent Transactions");
        List<Transaction> recent = budgetManager.getRecentTransactions(5);
        if (recent.isEmpty()) {
            card.getChildren().add(emptyLabel("No transactions yet"));
        } else {
            for (Transaction t : recent) {
                HBox row = new HBox();
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 0, 8, 0));
                row.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

                VBox info = new VBox(2);
                Label desc = new Label(t.getDescription());
                desc.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                desc.setTextFill(Color.web(TEXT_DARK));
                Label cat = new Label(t.getCategory().getDisplayName() + " · " + t.getDate().format(DATE_FMT));
                cat.setFont(Font.font(11));
                cat.setTextFill(Color.web(TEXT_LIGHT));
                info.getChildren().addAll(desc, cat);

                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                boolean isIncome = t.getType() == TransactionType.INCOME;
                Label amt = new Label((isIncome ? "+" : "-") + " ₹" + String.format("%.2f", t.getAmount()));
                amt.setFont(Font.font("System", FontWeight.BOLD, 13));
                amt.setTextFill(Color.web(isIncome ? INCOME_CLR : EXPENSE_CLR));

                row.getChildren().addAll(info, spacer, amt);
                card.getChildren().add(row);
            }
        }
        return card;
    }

    private VBox buildUpcomingDeadlines() {
        VBox card = card("📚 Upcoming Deadlines");
        List<Assignment> upcoming = deadlineManager.getUpcomingAssignments(4);
        if (upcoming.isEmpty()) {
            card.getChildren().add(emptyLabel("No upcoming deadlines 🎉"));
        } else {
            for (Assignment a : upcoming) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 0, 8, 0));
                row.setStyle("-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

                Rectangle bar = new Rectangle(4, 36);
                bar.setArcWidth(4); bar.setArcHeight(4);
                bar.setFill(Color.web(priorityColor(a.getPriority())));

                VBox info = new VBox(2);
                Label title = new Label(a.getTitle());
                title.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                title.setTextFill(Color.web(TEXT_DARK));
                Label sub = new Label(a.getSubject() + " · " + a.getDueDate().format(DATE_FMT));
                sub.setFont(Font.font(11));
                sub.setTextFill(Color.web(a.isOverdue() ? DANGER : TEXT_LIGHT));
                info.getChildren().addAll(title, sub);

                row.getChildren().addAll(bar, info);
                card.getChildren().add(row);
            }
        }
        return card;
    }

    private VBox buildSpendingChart() {
        VBox card = card("📊 Spending by Category (All Time)");
        Map<Category, Double> data = budgetManager.getExpensesByCategory();
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        if (max == 0) {
            card.getChildren().add(emptyLabel("No expenses recorded yet"));
            return card;
        }

        VBox bars = new VBox(10);
        for (Map.Entry<Category, Double> entry : data.entrySet()) {
            if (entry.getValue() == 0) continue;
            double pct = entry.getValue() / max;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label catLabel = new Label(entry.getKey().getDisplayName());
            catLabel.setMinWidth(130);
            catLabel.setFont(Font.font(12));
            catLabel.setTextFill(Color.web(TEXT_MID));

            StackPane barContainer = new StackPane();
            barContainer.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(barContainer, javafx.scene.layout.Priority.ALWAYS);

            Rectangle bg = new Rectangle(1, 16);
            bg.setFill(Color.web("#e2e8f0"));
            bg.setArcWidth(8); bg.setArcHeight(8);
            bg.widthProperty().bind(barContainer.widthProperty());

            Rectangle fill = new Rectangle(1, 16);
            fill.setFill(Color.web(ACCENT));
            fill.setArcWidth(8); fill.setArcHeight(8);
            fill.widthProperty().bind(barContainer.widthProperty().multiply(pct));

            barContainer.getChildren().addAll(bg, fill);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);

            Label amt = new Label("₹" + String.format("%.0f", entry.getValue()));
            amt.setMinWidth(70);
            amt.setAlignment(Pos.CENTER_RIGHT);
            amt.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
            amt.setTextFill(Color.web(TEXT_DARK));

            row.getChildren().addAll(catLabel, barContainer, amt);
            bars.getChildren().add(row);
        }
        card.getChildren().add(bars);
        return card;
    }

    // ─── Budget View ────────────────────────────────────────────────────────────

    private void showBudget() {
        contentArea.getChildren().setAll(buildBudgetContent());
    }

    private BorderPane buildBudgetContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + PAGE_BG + ";");
        root.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(2);
        titles.getChildren().addAll(pageTitle("Budget"), pageSubtitle("Track your income and expenses"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button addBtn = primaryButton("+ Add Transaction");
        addBtn.setOnAction(e -> { showAddTransactionDialog(); showBudget(); });
        header.getChildren().addAll(titles, spacer, addBtn);

        // Summary strip
        HBox summary = new HBox(16);
        summary.setPadding(new Insets(16, 0, 0, 0));
        summary.getChildren().addAll(
            summaryChip("Balance", fmt(budgetManager.getTotalBalance()), budgetManager.getTotalBalance() >= 0 ? SUCCESS : DANGER),
            summaryChip("Total Income", fmt(budgetManager.getTotalIncome()), INCOME_CLR),
            summaryChip("Total Expenses", fmt(budgetManager.getTotalExpenses()), EXPENSE_CLR),
            summaryChip("This Month", fmt(budgetManager.getMonthlyExpenses()), WARNING)
        );

        // Table
        TableView<Transaction> table = buildTransactionTable();

        VBox top = new VBox(16);
        top.getChildren().addAll(header, summary);
        root.setTop(top);
        root.setCenter(table);
        BorderPane.setMargin(table, new Insets(16, 0, 0, 0));
        return root;
    }

    private HBox summaryChip(String label, String value, String color) {
        HBox chip = new HBox(8);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setPadding(new Insets(12, 20, 12, 20));
        chip.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        VBox info = new VBox(2);
        Label lbl = new Label(label);
        lbl.setFont(Font.font(11));
        lbl.setTextFill(Color.web(TEXT_MID));
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 16));
        val.setTextFill(Color.web(color));
        info.getChildren().addAll(lbl, val);
        chip.getChildren().add(info);
        return chip;
    }

    @SuppressWarnings("unchecked")
    private TableView<Transaction> buildTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        table.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3); " +
                "-fx-border-color: transparent;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDate().format(DATE_FMT)));
        dateCol.setMaxWidth(120);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory().getDisplayName()));
        catCol.setMaxWidth(160);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getType().getDisplayName()));
        typeCol.setMaxWidth(100);
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-text-fill: " + (item.equals("Income") ? INCOME_CLR : EXPENSE_CLR) +
                            "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaction, String> amtCol = new TableColumn<>("Amount (₹)");
        amtCol.setCellValueFactory(data -> {
            Transaction t = data.getValue();
            String prefix = t.getType() == TransactionType.INCOME ? "+" : "-";
            return new javafx.beans.property.SimpleStringProperty(
                    prefix + String.format("%.2f", t.getAmount()));
        });
        amtCol.setMaxWidth(130);
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                            (item.startsWith("+") ? INCOME_CLR : EXPENSE_CLR) + ";");
                }
            }
        });

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Action");
        actionCol.setMaxWidth(90);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            { deleteBtn.setStyle("-fx-background-color: " + DANGER + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 6; -fx-font-size: 11; -fx-cursor: hand;");
              deleteBtn.setOnAction(e -> {
                  Transaction t = getTableView().getItems().get(getIndex());
                  budgetManager.removeTransaction(t.getId());
                  getTableView().getItems().remove(t);
                  showBudget();
              });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().addAll(dateCol, descCol, catCol, typeCol, amtCol, actionCol);

        ObservableList<Transaction> items = FXCollections.observableArrayList(
                budgetManager.getAllTransactions().stream()
                        .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                        .toList());
        table.setItems(items);
        table.setPlaceholder(new Label("No transactions yet. Click '+ Add Transaction' to get started."));
        return table;
    }

    private void showAddTransactionDialog() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Transaction");
        dialog.setHeaderText(null);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-font-size: 13;");

        TextField descField = styledTextField("e.g. Lunch, Books...");
        TextField amtField  = styledTextField("0.00");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(220);
        ComboBox<Category> catBox = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        catBox.setValue(Category.OTHER);
        catBox.setPrefWidth(220);
        ComboBox<TransactionType> typeBox = new ComboBox<>(FXCollections.observableArrayList(TransactionType.values()));
        typeBox.setValue(TransactionType.EXPENSE);
        typeBox.setPrefWidth(220);

        grid.addRow(0, formLabel("Description"), descField);
        grid.addRow(1, formLabel("Amount (₹)"), amtField);
        grid.addRow(2, formLabel("Date"), datePicker);
        grid.addRow(3, formLabel("Category"), catBox);
        grid.addRow(4, formLabel("Type"), typeBox);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addButtonType) {
                try {
                    String desc = descField.getText().trim();
                    double amt  = Double.parseDouble(amtField.getText().trim());
                    if (desc.isEmpty()) return null;
                    return new Transaction(desc, amt, catBox.getValue(), datePicker.getValue(), typeBox.getValue());
                } catch (NumberFormatException e) {
                    showError("Please enter a valid amount.");
                    return null;
                }
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(budgetManager::addTransaction);
    }

    // ─── Deadlines View ─────────────────────────────────────────────────────────

    private void showDeadlines() {
        contentArea.getChildren().setAll(buildDeadlineContent());
    }

    private BorderPane buildDeadlineContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + PAGE_BG + ";");
        root.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(2);
        titles.getChildren().addAll(pageTitle("Deadlines"), pageSubtitle("Manage your assignments and tasks"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button addBtn = primaryButton("+ Add Assignment");
        addBtn.setOnAction(e -> { showAddAssignmentDialog(); showDeadlines(); });
        header.getChildren().addAll(titles, spacer, addBtn);

        // Filter tabs
        HBox filters = new HBox(8);
        filters.setPadding(new Insets(16, 0, 0, 0));
        ToggleGroup tg = new ToggleGroup();
        ToggleButton allBtn      = filterTab("All",      tg);
        ToggleButton pendingBtn  = filterTab("Pending",  tg);
        ToggleButton overdueBtn  = filterTab("Overdue",  tg);
        ToggleButton doneBtn     = filterTab("Completed",tg);
        allBtn.setSelected(true);
        filters.getChildren().addAll(allBtn, pendingBtn, overdueBtn, doneBtn);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox cardList = buildAssignmentCards(deadlineManager.getAllAssignments());
        scroll.setContent(cardList);

        allBtn.setOnAction(e     -> scroll.setContent(buildAssignmentCards(deadlineManager.getAllAssignments())));
        pendingBtn.setOnAction(e -> scroll.setContent(buildAssignmentCards(deadlineManager.getPendingAssignments())));
        overdueBtn.setOnAction(e -> scroll.setContent(buildAssignmentCards(deadlineManager.getOverdueAssignments())));
        doneBtn.setOnAction(e    -> scroll.setContent(buildAssignmentCards(
                deadlineManager.getAllAssignments().stream().filter(a -> a.isCompleted()).toList())));

        VBox top = new VBox(0);
        top.getChildren().addAll(header, filters);
        root.setTop(top);
        root.setCenter(scroll);
        BorderPane.setMargin(scroll, new Insets(16, 0, 0, 0));
        return root;
    }

    private VBox buildAssignmentCards(List<Assignment> assignments) {
        VBox list = new VBox(12);
        list.setPadding(new Insets(4, 0, 0, 0));
        list.setStyle("-fx-background-color: transparent;");

        if (assignments.isEmpty()) {
            VBox empty = new VBox();
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60));
            Label lbl = new Label("🎉  Nothing here!");
            lbl.setFont(Font.font(16));
            lbl.setTextFill(Color.web(TEXT_LIGHT));
            empty.getChildren().add(lbl);
            list.getChildren().add(empty);
            return list;
        }

        for (Assignment a : assignments) {
            list.getChildren().add(buildAssignmentCard(a));
        }
        return list;
    }

    private HBox buildAssignmentCard(Assignment a) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 0));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3);");

        // Priority bar
        Rectangle bar = new Rectangle(6, 60);
        bar.setFill(Color.web(priorityColor(a.getPriority())));
        // round left corners via clip - approximate with style on container
        bar.setArcWidth(12); bar.setArcHeight(12);

        // Checkbox
        CheckBox cb = new CheckBox();
        cb.setSelected(a.isCompleted());
        cb.setStyle("-fx-cursor: hand;");
        cb.setOnAction(e -> {
            deadlineManager.toggleComplete(a.getId());
            showDeadlines();
        });

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label titleLbl = new Label(a.getTitle());
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLbl.setTextFill(Color.web(a.isCompleted() ? TEXT_LIGHT : TEXT_DARK));
        if (a.isCompleted()) {
            titleLbl.setStyle("-fx-strikethrough: true;");
        }

        HBox meta = new HBox(12);
        Label subjectLbl = new Label("📖 " + a.getSubject());
        subjectLbl.setFont(Font.font(12));
        subjectLbl.setTextFill(Color.web(TEXT_MID));

        String dueText;
        String dueColor;
        if (a.isCompleted()) {
            dueText = "✅ Completed";
            dueColor = SUCCESS;
        } else if (a.isOverdue()) {
            dueText = "⚠ Overdue! Due " + a.getDueDate().format(DATE_FMT);
            dueColor = DANGER;
        } else if (a.isDueSoon()) {
            long days = a.getDaysUntilDue();
            dueText = "🔔 Due in " + days + " day" + (days == 1 ? "" : "s") + " (" + a.getDueDate().format(DATE_FMT) + ")";
            dueColor = WARNING;
        } else {
            dueText = "📅 Due " + a.getDueDate().format(DATE_FMT);
            dueColor = TEXT_MID;
        }

        Label dueLbl = new Label(dueText);
        dueLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        dueLbl.setTextFill(Color.web(dueColor));

        Label prioLbl = new Label(a.getPriority().getDisplayName());
        prioLbl.setFont(Font.font(11));
        prioLbl.setTextFill(Color.web(priorityColor(a.getPriority())));
        prioLbl.setPadding(new Insets(2, 8, 2, 8));
        prioLbl.setStyle("-fx-background-color: " + priorityColor(a.getPriority()) + "22; " +
                "-fx-background-radius: 12;");

        meta.getChildren().addAll(subjectLbl, dueLbl, prioLbl);
        info.getChildren().addAll(titleLbl, meta);

        if (a.getDescription() != null && !a.getDescription().isBlank()) {
            Label descLbl = new Label(a.getDescription());
            descLbl.setFont(Font.font(12));
            descLbl.setTextFill(Color.web(TEXT_LIGHT));
            descLbl.setWrapText(true);
            info.getChildren().add(descLbl);
        }

        // Delete button
        Button del = new Button("🗑");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: " + DANGER + "; " +
                "-fx-font-size: 15; -fx-cursor: hand; -fx-border-color: transparent;");
        del.setOnAction(e -> {
            deadlineManager.removeAssignment(a.getId());
            showDeadlines();
        });

        card.getChildren().addAll(bar, cb, info, del);
        return card;
    }

    private void showAddAssignmentDialog() {
        Dialog<Assignment> dialog = new Dialog<>();
        dialog.setTitle("Add Assignment");
        dialog.setHeaderText(null);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField titleField   = styledTextField("e.g. Math Assignment 3");
        TextField subjectField = styledTextField("e.g. Mathematics");
        DatePicker datePicker  = new DatePicker(LocalDate.now().plusDays(7));
        datePicker.setPrefWidth(220);
        ComboBox<TaskPriority> prioBox = new ComboBox<>(FXCollections.observableArrayList(TaskPriority.values()));
        prioBox.setValue(TaskPriority.MEDIUM);
        prioBox.setPrefWidth(220);
        TextArea descArea = new TextArea();
        descArea.setPromptText("Optional notes or description...");
        descArea.setPrefRowCount(3);
        descArea.setMaxWidth(220);
        descArea.setWrapText(true);

        grid.addRow(0, formLabel("Title"),       titleField);
        grid.addRow(1, formLabel("Subject"),     subjectField);
        grid.addRow(2, formLabel("Due Date"),    datePicker);
        grid.addRow(3, formLabel("Priority"),    prioBox);
        grid.addRow(4, formLabel("Notes"),       descArea);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addButtonType) {
                String title   = titleField.getText().trim();
                String subject = subjectField.getText().trim();
                if (title.isEmpty() || subject.isEmpty()) {
                    showError("Title and Subject are required.");
                    return null;
                }
                return new Assignment(title, subject, datePicker.getValue(),
                        prioBox.getValue(), descArea.getText().trim());
            }
            return null;
        });

        Optional<Assignment> result = dialog.showAndWait();
        result.ifPresent(deadlineManager::addAssignment);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private VBox card(String title) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3);");
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLbl.setTextFill(Color.web(TEXT_DARK));
        card.getChildren().add(titleLbl);
        return card;
    }

    private Label pageTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 26));
        l.setTextFill(Color.web(TEXT_DARK));
        return l;
    }

    private Label pageSubtitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(13));
        l.setTextFill(Color.web(TEXT_MID));
        return l;
    }

    private Label emptyLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(13));
        l.setTextFill(Color.web(TEXT_LIGHT));
        l.setPadding(new Insets(20, 0, 10, 0));
        return l;
    }

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 13; " +
                "-fx-padding: 10 20; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(ACCENT, ACCENT_LIGHT)));
        btn.setOnMouseExited(e  -> btn.setStyle(btn.getStyle().replace(ACCENT_LIGHT, ACCENT)));
        return btn;
    }

    private ToggleButton filterTab(String text, ToggleGroup tg) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(tg);
        btn.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 8; " +
                "-fx-border-color: #e2e8f0; -fx-border-radius: 8; " +
                "-fx-padding: 7 16; -fx-cursor: hand; -fx-font-size: 12;");
        btn.selectedProperty().addListener((obs, o, selected) -> {
            if (selected) btn.setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-border-color: transparent; " +
                    "-fx-padding: 7 16; -fx-cursor: hand; -fx-font-size: 12; -fx-font-weight: bold;");
            else btn.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 8; " +
                    "-fx-border-color: #e2e8f0; -fx-border-radius: 8; " +
                    "-fx-padding: 7 16; -fx-cursor: hand; -fx-font-size: 12;");
        });
        return btn;
    }

    private TextField styledTextField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(220);
        f.setStyle("-fx-background-radius: 6; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 7 10;");
        return f;
    }

    private Label formLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        l.setTextFill(Color.web(TEXT_MID));
        l.setMinWidth(90);
        return l;
    }

    private String priorityColor(TaskPriority p) {
        return switch (p) {
            case HIGH   -> DANGER;
            case MEDIUM -> WARNING;
            case LOW    -> SUCCESS;
        };
    }

    private String fmt(double value) {
        return "₹" + String.format("%.2f", value);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
