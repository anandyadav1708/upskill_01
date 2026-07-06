package main;

import model.Expense;
import service.ExpenseService;
import utils.FileManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main class: Entry point for the Expense Tracker console application.
 * Presents a menu to the user and handles all user input and program flow.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExpenseService expenseService = new ExpenseService();

    public static void main(String[] args) {
        // Load expenses from file at the start
        try {
            List<Expense> loaded = FileManager.loadExpenses();
            for (Expense exp : loaded) {
                // Re-add to preserve incrementing IDs properly
                expenseService.addExpense(exp.getDate(), exp.getAmount(), exp.getCategory(), exp.getDescription());
            }
            System.out.println("Loaded " + loaded.size() + " expenses from disk.");
        } catch (Exception e) {
            System.out.println("Error loading expenses. Please check if the data file is accessible.");
        }

        while (true) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addExpenseFlow();
                    break;
                case "2":
                    showAllExpenses();
                    break;
                case "3":
                    searchByCategoryFlow();
                    break;
                case "4":
                    updateExpenseFlow();
                    break;
                case "5":
                    deleteExpenseFlow();
                    break;
                case "6":
                    displayTotalExpense();
                    break;
                case "7":
                    exitApp();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 7.");
            }
        }
    }

    /** Prints the application menu with formatted borders. */
    private static void printMenu() {
        System.out.println("\n===== Expense Tracker Menu =====");
        System.out.println("1. Add Expense");
        System.out.println("2. View Expenses");
        System.out.println("3. Search by Category");
        System.out.println("4. Update Expense");
        System.out.println("5. Delete Expense");
        System.out.println("6. Display Total Expense");
        System.out.println("7. Exit");
        System.out.println("===============================");
        System.out.print("Enter your choice: ");
    }

    /** Handles adding a new expense with validation and exception handling. */
    private static void addExpenseFlow() {
        try {
            System.out.print("Enter date (yyyy-mm-dd): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            double amount = promptForAmount();
            String category = promptForNonEmptyString("Enter category: ");
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            Expense expense = expenseService.addExpense(date, amount, category, description);
            System.out.println("Expense added: " + expense);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-mm-dd.");
            
        } catch (Exception e) {
            System.out.println("Error adding expense: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a valid, positive double amount and returns it.
     * Ensures only a numeric, >0 input is accepted.
     */
    private static double promptForAmount() {
        double amount = 0.0;
        while (true) {
            System.out.print("Enter amount: ");
            String input = scanner.nextLine();
            try {
                amount = Double.parseDouble(input);
                if (amount <= 0) {
                    System.out.println("Amount must be greater than zero. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
            }
        }
        return amount;
    }

    /**
     * Prompts for a non-empty, trimmed string with the given prompt message.
     */
    private static String promptForNonEmptyString(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) break;
            System.out.println("This field cannot be empty. Try again.");
        }
        return input;
    }

    /** 
     * Shows all expenses in a formatted table. 
     * If no expenses exist, informs the user.
     */
    private static void showAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            printExpenseTableHeader();
            for (Expense exp : expenses) {
                printExpenseTableRow(exp);
            }
            printExpenseTableFooter();
        }
    }

    /** Prints the table header for the expenses list. */
    private static void printExpenseTableHeader() {
        System.out.println("+-----+------------+--------+--------------+----------------------+" );
        System.out.printf("| %-3s | %-10s | %-6s | %-12s | %-20s |%n", "ID", "Date", "Amount", "Category", "Description");
        System.out.println("+-----+------------+--------+--------------+----------------------+" );
    }

    /** Prints a formatted expense table row. */
    private static void printExpenseTableRow(Expense e) {
        System.out.printf("| %-3d | %-10s | %6.2f | %-12s | %-20s |%n", e.getId(), e.getDate(), e.getAmount(), e.getCategory(), e.getDescription());
    }

    /** Prints the table footer. */
    private static void printExpenseTableFooter() {
        System.out.println("+-----+------------+--------+--------------+----------------------+" );
    }

    /**
     * Handles searching expenses by category, displays results in a table or gives a no-results message.
     */
    private static void searchByCategoryFlow() {
        String category = promptForNonEmptyString("Enter category to search: ");
        List<Expense> matches = expenseService.searchByCategory(category);
        if (matches.isEmpty()) {
            System.out.println("No expenses found under category '" + category + "'.");
        } else {
            printExpenseTableHeader();
            for (Expense exp : matches) {
                printExpenseTableRow(exp);
            }
            printExpenseTableFooter();
        }
    }

    /** Handles updating an expense with validation. */
    private static void updateExpenseFlow() {
        try {
            System.out.print("Enter Expense ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());
            Expense target = expenseService.findById(id);
            if (target == null) {
                System.out.println("Expense with ID " + id + " not found.");
                return;
            }
            System.out.print("Enter new date (yyyy-mm-dd) [current: " + target.getDate() + "]: ");
            String dateInput = scanner.nextLine();
            LocalDate date = dateInput.isEmpty() ? target.getDate() : LocalDate.parse(dateInput);
            double amount;
            while (true) {
                System.out.print("Enter new amount [current: " + target.getAmount() + "]: ");
                String amtInput = scanner.nextLine();
                if (amtInput.isEmpty()) {
                    amount = target.getAmount();
                    break;
                }
                try {
                    amount = Double.parseDouble(amtInput);
                    if (amount <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Amount must be a positive number. Try again.");
                }
            }
            String category;
            while (true) {
                System.out.print("Enter new category [current: " + target.getCategory() + "]: ");
                String catInput = scanner.nextLine().trim();
                if (catInput.isEmpty()) {
                    category = target.getCategory();
                    break;
                } else if (!catInput.isEmpty()) {
                    category = catInput;
                    break;
                }
                System.out.println("Category cannot be empty. Try again.");
            }
            System.out.print("Enter new description [current: " + target.getDescription() + "]: ");
            String descInput = scanner.nextLine();
            String description = descInput.isEmpty() ? target.getDescription() : descInput;
            boolean updated = expenseService.updateExpense(id, date, amount, category, description);
            if (updated) {
                System.out.println("Expense updated successfully.");
            } else {
                System.out.println("Update failed.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-mm-dd.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount or ID entered.");
        } catch (Exception e) {
            System.out.println("Error updating expense: " + e.getMessage());
        }
    }

    /** Handles deleting an expense. */
    private static void deleteExpenseFlow() {
        try {
            System.out.print("Enter Expense ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            boolean removed = expenseService.deleteExpense(id);
            if (removed) {
                System.out.println("Expense deleted successfully.");
            } else {
                System.out.println("Expense with ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID entered.");
        } catch (Exception e) {
            System.out.println("Error deleting expense: " + e.getMessage());
        }
    }

    /** Displays the total expense amount. */
    private static void displayTotalExpense() {
        double total = expenseService.getTotalExpense();
        System.out.printf("Total Expense: %.2f\n", total);
    }

    /** Handles program exit and saving data. */
    private static void exitApp() {
        try {
            FileManager.saveExpenses(expenseService.getAllExpenses());
            System.out.println("Expenses saved. Exiting application. Bye!");
        } catch (Exception e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
        scanner.close();
    }
}