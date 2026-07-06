package service;

import model.Expense;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles business logic for managing expenses.
 * Provides methods to add, view, search, update, delete expenses, and calculate the total expense.
 */
public class ExpenseService {
    private List<Expense> expenses;
    private int nextId;

    public ExpenseService() {
        expenses = new ArrayList<>();
        nextId = 1; // Start IDs from 1
    }

    /**
     * Adds a new expense to the list.
     */
    public Expense addExpense(LocalDate date, double amount, String category, String description) {
        Expense expense = new Expense(nextId++, date, amount, category, description);
        expenses.add(expense);
        return expense;
    }

    /**
     * Returns all expenses.
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Searches expenses by category (case-insensitive). Returns a list of matching expenses.
     */
    public List<Expense> searchByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Updates an expense by id. Returns true if update was successful.
     */
    public boolean updateExpense(int id, LocalDate date, double amount, String category, String description) {
        for (Expense e : expenses) {
            if (e.getId() == id) {
                e.setDate(date);
                e.setAmount(amount);
                e.setCategory(category);
                e.setDescription(description);
                return true;
            }
        }
        return false; // Expense not found
    }

    /**
     * Deletes an expense by id. Returns true if removal was successful.
     */
    public boolean deleteExpense(int id) {
        return expenses.removeIf(e -> e.getId() == id);
    }

    /**
     * Calculates the total amount of all expenses.
     */
    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    /**
     * Finds an expense by id. Returns the expense or null if not found.
     */
    public Expense findById(int id) {
        for (Expense e : expenses) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}