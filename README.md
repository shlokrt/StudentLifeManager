
## BYOP Project Submission of Programming in Java for VITyarthi

**Name:** **Shlok** <br>

---
# Student Life Manager

A simple Java desktop application that helps students manage both their **budget** and **assignment deadlines** in one unified dashboard.
---

## Features

### Dashboard
- Combined overview of your financial and academic status
- Alert banner for overdue assignments or upcoming deadlines
- Stat cards: Balance, Total Income, Total Expenses, Pending Tasks
- Recent transactions list
- Upcoming deadlines list
- Spending breakdown by category with visual bars

### Budget Manager
- Add income and expense transactions
- Categorize spending (Food, Study, Transport, Entertainment, Health, Bills, Other)
- Monthly summary (income vs. expenses)
- Running balance tracker
- Delete transactions

### Deadline Manager
- Add assignments with subject, due date, priority, and notes
- Priority levels: High  / Medium  / Low 
- Smart status indicators: overdue , due soon , completed 
- Filter view: All / Pending / Overdue / Completed
- Toggle completion with one click
- Delete assignments

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 20+ | Core language |
| JavaFX 21 | GUI framework |
| Gson 2.10.1 | JSON data persistence |
| Maven | Build system |

---

## How to Run

### Prerequisites
- Java 20 or higher installed
- Maven installed (`mvn -version` to check)

### Steps

```bash
# 1. Clone or download the project
git clone https://github.com/shlokrt/StudentLifeManager
cd StudentLifeManager

# 2. Run with Maven
mvn javafx:run
```

### Alternative: Build and run JAR
```bash
mvn package
java -jar target/StudentLifeManager-1.0-SNAPSHOT.jar
```

---

## Project Structure

```
StudentLifeManager/
├── pom.xml                          # Maven build config
├── README.md
└── src/main/java/com/studentlife/
    ├── Main.java                    # Entry point
    ├── model/
    │   ├── Transaction.java         # Budget transaction entity
    │   ├── Assignment.java          # Assignment entity
    │   ├── Category.java            # Expense category enum
    │   ├── TaskPriority.java            # Assignment priority enum
    │   └── TransactionType.java     # Income / Expense enum
    ├── manager/
    │   ├── BudgetManager.java       # Budget business logic
    │   └── DeadlineManager.java     # Deadline business logic
    ├── storage/
    │   └── DataStorage.java         # Gson-based JSON persistence
    └── ui/
        └── MainWindow.java          # Full JavaFX UI
```

---

## Data Persistence

All data is saved automatically to:
- **Windows:** `C:\Users\<you>\.studentlifemanager\`
- **Mac/Linux:** `~/.studentlifemanager/`

Two JSON files are created:
- `transactions.json`
- `assignments.json`

---

## Java Concepts Demonstrated

| Concept | Where Used |
|---|---|
| OOP / Encapsulation | All model classes |
| Enums | Category, Priority, TransactionType |
| Collections & Streams | Manager classes (filtering, sorting) |
| File I/O | DataStorage (read/write JSON) |
| Exception Handling | Storage, number parsing in dialogs |
| Java `LocalDate` | Due date comparison, overdue detection |
| Generics | DataStorage type tokens |
| JavaFX UI | Full application frontend |
| Lambda Expressions | Stream operations, event handlers |

---

## Author

**Name:** **Shlok** <br>
  
BYOP Project of Programming in Java for VITyarthi Submission
