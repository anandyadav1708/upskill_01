package utils;

import model.Expense;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for saving and loading Expense list data to/from a file.
 * Simplifies rudimentary persistence using plain text (CSV-like format).
 */
public class FileManager {
    private static final String FILE_NAME = "expenses.txt";

    /**
     * Saves the given list of expenses to a file.
     */
    public static void saveExpenses(List<Expense> expenses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense expense : expenses) {
                // Format: id,date,amount,category,description\n
                // To handle commas safely, replace with another character in free-text fields
                writer.write(expense.getId() + ","
                        + expense.getDate() + ","
                        + expense.getAmount() + ","
                        + expense.getCategory().replace(",", ";") + ","
                        + expense.getDescription().replace(",", ";") + "\n");
            }
        }
    }

    /**
     * Loads a list of expenses from a file.
     */
    public static List<Expense> loadExpenses() throws IOException {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return expenses; // No previously stored data

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1]);
                    double amount = Double.parseDouble(parts[2]);
                    String category = parts[3];
                    String description = parts[4];
                    expenses.add(new Expense(id, date, amount, category, description));
                }
            }
        }
        return expenses;
    }
}