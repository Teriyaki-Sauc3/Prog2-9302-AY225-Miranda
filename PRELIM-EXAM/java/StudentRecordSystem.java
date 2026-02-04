// John Paul Benedict G. Miranda
// 23-0332-752

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class StudentRecordSystem extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, gradeField;
    private JButton addButton, deleteButton;
    private static final String CSV_FILE = "MOCK_DATA.csv";
    private static String CSV_PATH;
    
    static {
        // Find CSV file - works on any computer
        File csvFile = null;
        
        // 1. Try: Get location of the .class file (most reliable for portability)
        try {
            String classPath = StudentRecordSystem.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
            if (classPath.matches("^/[A-Za-z]:.*")) {
                classPath = classPath.substring(1);
            }
            File classDir = new File(classPath);
            if (classDir.isFile()) classDir = classDir.getParentFile();
            csvFile = new File(classDir, CSV_FILE);
            if (csvFile.exists()) {
                CSV_PATH = csvFile.getAbsolutePath();
            }
        } catch (Exception e) {
            // Ignore and try fallback
        }
        
        // 2. Fallback: current working directory
        if (CSV_PATH == null || !new File(CSV_PATH).exists()) {
            csvFile = new File(System.getProperty("user.dir"), CSV_FILE);
            if (csvFile.exists()) {
                CSV_PATH = csvFile.getAbsolutePath();
            }
        }
        
        // 3. Final fallback
        if (CSV_PATH == null) {
            CSV_PATH = new File(System.getProperty("user.dir"), CSV_FILE).getAbsolutePath();
        }
    }

    // Yellow color scheme
    private static final Color PRIMARY_YELLOW = new Color(255, 193, 7);
    private static final Color DARK_YELLOW = new Color(255, 179, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color DARK_TEXT = new Color(51, 51, 51);
    private static final Color DELETE_RED = new Color(244, 67, 54);
    private static final Color DELETE_RED_HOVER = new Color(218, 25, 11);

    public StudentRecordSystem() {
        setTitle("Records - John Paul Benedict G. Miranda 23-0332-752");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create table model with 3 columns: ID, Name, Grade
        String[] columns = {"ID", "Name", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // Style the table
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(221, 221, 221));
        table.setSelectionBackground(PRIMARY_YELLOW);
        table.setSelectionForeground(DARK_TEXT);
        
        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_YELLOW);
        header.setForeground(DARK_TEXT);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE);

        // Create input panel with 3 JTextFields: ID, Name, Grade
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        inputPanel.setBackground(WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(221, 221, 221)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        idField = createStyledTextField(12);
        nameField = createStyledTextField(15);
        gradeField = createStyledTextField(8);

        inputPanel.add(createStyledLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(createStyledLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Grade:"));
        inputPanel.add(gradeField);

        // Create button panel with Add and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        addButton = createStyledButton("Add", PRIMARY_YELLOW, DARK_TEXT);
        deleteButton = createStyledButton("Delete", DELETE_RED, WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Add action listeners
        addButton.addActionListener(e -> addRecord());
        deleteButton.addActionListener(e -> deleteRecord());

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load CSV data on startup
        loadCSVData();
    }

    // Helper method to create styled text fields
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(221, 221, 221)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    // Helper method to create styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(DARK_TEXT);
        return label;
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        Color hoverColor = bgColor.equals(PRIMARY_YELLOW) ? DARK_YELLOW : DELETE_RED_HOVER;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    // Load CSV data on startup with try-catch for file-reading errors
    private void loadCSVData() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CSV_PATH));
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Split the string and populate the DefaultTableModel
                String[] data = line.split(",");
                String id = data.length > 0 ? data[0].trim() : "";
                String name = data.length > 1 ? data[1].trim() : "";
                String grade = data.length > 2 ? data[2].trim() : "";

                Object[] row = {id, name, grade};
                tableModel.addRow(row);
            }
            br.close();
        } catch (IOException e) {
            // Handle file-reading errors with try-catch
            JOptionPane.showMessageDialog(this,
                "Error reading CSV file: " + CSV_PATH + "\n\n" + e.getMessage() +
                "\n\nMake sure MOCK_DATA.csv is in the same folder as the .java file!",
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Create: Add a new row to the table using the text field inputs
    private void addRecord() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String grade = gradeField.getText().trim();

        if (!id.isEmpty() && !name.isEmpty() && !grade.isEmpty()) {
            tableModel.addRow(new Object[]{id, name, grade});
            saveToCSV();
            
            // Clear fields
            idField.setText("");
            nameField.setText("");
            gradeField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields (ID, Name, Grade)", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // Delete: Remove the selected row from the table
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            saveToCSV();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a row to delete", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // Save data back to CSV
    private void saveToCSV() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH));
            // Write header
            pw.println("ID,Name,Grade");
            
            // Write data rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String id = tableModel.getValueAt(i, 0).toString();
                String name = tableModel.getValueAt(i, 1).toString();
                String grade = tableModel.getValueAt(i, 2).toString();
                pw.println(id + "," + name + "," + grade);
            }
            pw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error writing to CSV file: " + e.getMessage(), 
                "File Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentRecordSystem frame = new StudentRecordSystem();
            frame.setVisible(true);
        });
    }
}