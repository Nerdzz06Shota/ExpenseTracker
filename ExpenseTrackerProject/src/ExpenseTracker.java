import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;

public class ExpenseTracker extends JFrame {
    // GUI components
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField dateField;
    private JTextField descriptionField;
    private JTextArea expenseListArea;
    
    // In-memory list to keep track of expenses during this session
    private java.util.List<String> expenses;
    
    public ExpenseTracker() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);  // Center the window
        
        expenses = new ArrayList<>();
        
        // Main panel and layout
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10,10,10,10));
        panel.setLayout(new BorderLayout(10, 10));
        setContentPane(panel);
        
        // Input panel at top with form fields and labels
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        inputPanel.add(new JLabel("Amount (e.g., 12.50):"));
        amountField = new JTextField();
        inputPanel.add(amountField);
        
        inputPanel.add(new JLabel("Category (e.g., Food):"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);
        
        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));  // default today
        inputPanel.add(dateField);
        
        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);
        
        // Buttons panel for Add Expense and Save
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton addButton = new JButton("Add Expense");
        JButton saveButton = new JButton("Save to File");
        buttonsPanel.add(addButton);
        buttonsPanel.add(saveButton);
        
        inputPanel.add(new JLabel(""));  // placeholder for grid alignment
        inputPanel.add(buttonsPanel);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Text area for displaying expense list
        expenseListArea = new JTextArea();
        expenseListArea.setEditable(false);
        expenseListArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(expenseListArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action: Add Expense button
        addButton.addActionListener(e -> addExpense());
        
        // Action: Save button
        saveButton.addActionListener(e -> saveExpensesToFile());
    }
    
    // Add expense input to list and display
    private void addExpense() {
        // Get inputs
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        String dateText = dateField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (amountText.isEmpty() || category.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount, Category and Date are required fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if(amount < 0){
                throw new NumberFormatException("Negative amount");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for Amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate date format
        if (!isValidDate(dateText)) {
            JOptionPane.showMessageDialog(this, "Please enter the Date in yyyy-MM-dd format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Format expense line string
        String expenseLine = String.format("%-10s | %-15s | %-12s | %s", 
                String.format("%.2f", amount), category, dateText, description);
        
        expenses.add(expenseLine);
        
        // Update textarea display
        refreshExpenseListArea();
        
        // Clear some fields for new entry
        amountField.setText("");
        descriptionField.setText("");
    }
    
    // Refresh the expense list display area with header
    private void refreshExpenseListArea() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-15s | %-12s | %s%n", "Amount", "Category", "Date", "Description"));
        sb.append("-------------------------------------------------------------------\n");
        for (String exp : expenses) {
            sb.append(exp).append("\n");
        }
        expenseListArea.setText(sb.toString());
    }
    
    // Save all expenses to a text file in the program directory
    private void saveExpensesToFile() {
        if(expenses.isEmpty()){
            JOptionPane.showMessageDialog(this, "No expenses to save.", "Nothing to save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File file = new File("expenses.txt");
        try(PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {  // append mode
            for(String expense : expenses){
                writer.println(expense);
            }
            writer.println(); // Blank line separator for sessions
            JOptionPane.showMessageDialog(this, "Expenses saved to " + file.getAbsolutePath(), "Saved Successfully", JOptionPane.INFORMATION_MESSAGE);
            expenses.clear();
            refreshExpenseListArea();
        } catch(IOException e){
            JOptionPane.showMessageDialog(this, "Error saving expenses: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Check if date string matches yyyy-MM-dd format
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch(ParseException e){
            return false;
        }
    }
    
    public static void main(String args[]) {
        // Start GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            ExpenseTracker tracker = new ExpenseTracker();
            tracker.setVisible(true);
        });
    }
}



