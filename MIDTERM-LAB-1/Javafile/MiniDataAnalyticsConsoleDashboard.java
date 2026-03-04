import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// Separate class for data records (modular design requirement)
class DataRecord {
    private String[] values;
    private String[] headers;
    
    public DataRecord(String[] values, String[] headers) {
        this.values = values;
        this.headers = headers;
    }
    
    public String getValue(int index) {
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        return "";
    }
    
    public String[] getValues() {
        return values;
    }
    
    public int getColumnCount() {
        return values.length;
    }
    
    public double getNumericValue(int index) {
        if (index < 0 || index >= values.length) return 0;
        String val = values[index].replaceAll("[^0-9.-]", "");
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

public class MiniDataAnalyticsConsoleDashboard {
    
    // Dynamic dataset storage using DataRecord class
    static ArrayList<DataRecord> salesData = new ArrayList<>();
    static String[] headers;
    static String filePath;
    
    // Column indices (will be detected from headers)
    static int customerCol = -1;
    static int categoryCol = -1;
    static int monthCol = -1;
    static int amountCol = -1;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("  MINI DATA ANALYTICS CONSOLE DASHBOARD");
        System.out.println("========================================");
        System.out.println();
        
        // Step 1: Get valid file path from user (loops until valid)
        File file;
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = scanner.nextLine().trim();
            
            // Remove quotes if user copied path with quotes
            if (path.startsWith("\"") && path.endsWith("\"")) {
                path = path.substring(1, path.length() - 1);
            }
            
            // Validate: Check if path is empty
            if (path.isEmpty()) {
                System.out.println("Error: File path cannot be empty. Please try again.");
                continue;
            }
            
            file = new File(path);
            
            // Validate: Check if file exists
            if (!file.exists()) {
                System.out.println("Error: File does not exist. Please check the path and try again.");
                continue;
            }
            
            // Validate: Check if it's a file (not directory)
            if (!file.isFile()) {
                System.out.println("Error: Path is not a file. Please provide a valid file path.");
                continue;
            }
            
            // Validate: Check if file is readable
            if (!file.canRead()) {
                System.out.println("Error: File is not readable. Please check file permissions.");
                continue;
            }
            
            // Validate: Check if file is CSV format
            if (!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Error: File is not in CSV format. Please provide a .csv file.");
                continue;
            }
            
            // Validate: Check if CSV has valid content
            if (!validateCSVFormat(path)) {
                System.out.println("Error: Invalid CSV format. File must have headers and data rows.");
                continue;
            }
            
            filePath = path;
            break;
        }
        
        // Step 2: Load dataset into memory
        System.out.println("\nLoading dataset...");
        if (!loadDataset(filePath)) {
            System.out.println("Failed to load dataset. Exiting.");
            scanner.close();
            return;
        }
        
        System.out.println("Dataset loaded successfully!");
        System.out.println("Records found: " + salesData.size());
        System.out.println();
        
        // Step 3: Menu system
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number (1-5).");
                scanner.next();
                System.out.print("Enter your choice: ");
            }
            choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    viewDatasetSummary();
                    break;
                case 2:
                    monthlySales();
                    break;
                case 3:
                    topCustomers();
                    break;
                case 4:
                    categoryAnalysis();
                    break;
                case 5:
                    System.out.println("\nThanks for using the Analytics Dashboard!");
                    System.out.println("Goodbye!\n");
                    break;
                default:
                    System.out.println("\nInvalid choice! Please select 1-5.\n");
            }
        } while (choice != 5);
        
        scanner.close();
    }
    
    // Validate CSV format - check if file has headers and at least one data row
    public static boolean validateCSVFormat(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String headerLine = reader.readLine();
            String dataLine = reader.readLine();
            reader.close();
            
            // Check if file has at least header and one data row
            if (headerLine == null || headerLine.trim().isEmpty()) {
                return false;
            }
            
            // Check if header contains commas (CSV format)
            if (!headerLine.contains(",")) {
                return false;
            }
            
            // Check if there's at least one data row
            if (dataLine == null || dataLine.trim().isEmpty()) {
                return false;
            }
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    // Load CSV dataset into memory using DataRecord class
    public static boolean loadDataset(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] values = parseCSVLine(line);
                
                if (isFirstLine) {
                    headers = values;
                    detectColumns(headers);
                    isFirstLine = false;
                } else {
                    // Create DataRecord object for each row (modular design)
                    DataRecord record = new DataRecord(values, headers);
                    salesData.add(record);
                }
            }
            reader.close();
            
            // Validate required columns were found
            if (amountCol == -1) {
                System.out.println("Error: Could not detect sales/amount column.");
                System.out.println("Available columns: ");
                for (int i = 0; i < headers.length; i++) {
                    System.out.println("  " + i + ": " + headers[i]);
                }
                return false;
            }
            
            return salesData.size() > 0;
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }
    
    // Parse CSV line handling quoted values
    public static String[] parseCSVLine(String line) {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        
        return values.toArray(new String[0]);
    }
    
    // Auto-detect column indices from headers
    public static void detectColumns(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].toLowerCase().trim();
            
            // Customer/Name detection - also works for game titles, publishers
            if (h.contains("customer") || h.contains("name") || h.contains("buyer") || h.contains("client") || h.equals("title") || h.equals("publisher")) {
                if (customerCol == -1) customerCol = i;
            }
            // Category detection - also works for genre
            if (h.contains("category") || h.contains("product") || h.contains("type") || h.contains("item") || h.equals("genre") || h.equals("console")) {
                if (categoryCol == -1) categoryCol = i;
            }
            // Month/Date detection
            if (h.contains("month") || h.contains("date") || h.contains("period") || h.contains("year")) {
                if (monthCol == -1) monthCol = i;
            }
            // Amount/Sales detection - prioritize total_sales
            if (h.equals("total_sales") || h.contains("amount") || h.contains("sales") || h.contains("price") || h.contains("revenue")) {
                if (amountCol == -1) amountCol = i;
            }
        }
        
        System.out.println("\n Column Detection Results:");
        System.out.println(" Customer column: " + (customerCol >= 0 ? headers[customerCol] : "Not found"));
        System.out.println(" Category column: " + (categoryCol >= 0 ? headers[categoryCol] : "Not found"));
        System.out.println(" Month column: " + (monthCol >= 0 ? headers[monthCol] : "Not found"));
        System.out.println(" Amount column: " + (amountCol >= 0 ? headers[amountCol] : "Not found"));
    }
    
    // Get amount value from a DataRecord (handles currency symbols and commas)
    public static double getAmount(DataRecord record) {
        return record.getNumericValue(amountCol);
    }
    
    public static void displayMenu() {
        System.out.println("\n-------- MAIN MENU --------");
        System.out.println("1 - View Dataset Summary");
        System.out.println("2 - Monthly Sales");
        System.out.println("3 - Top Customers");
        System.out.println("4 - Category Analysis");
        System.out.println("5 - Exit");
        System.out.println("---------------------------");
    }
    
    public static void viewDatasetSummary() {
        System.out.println("\n=== DATASET SUMMARY ===");
        
        int totalRecords = salesData.size();
        double totalRevenue = 0;
        double minSale = Double.MAX_VALUE;
        double maxSale = Double.MIN_VALUE;
        
        // Get unique counts
        HashMap<String, Integer> uniqueCustomers = new HashMap<>();
        HashMap<String, Integer> uniqueCategories = new HashMap<>();
        HashMap<String, Integer> uniqueMonths = new HashMap<>();
        
        for (DataRecord record : salesData) {
            double amount = getAmount(record);
            totalRevenue += amount;
            if (amount < minSale) minSale = amount;
            if (amount > maxSale) maxSale = amount;
            
            if (customerCol >= 0 && customerCol < record.getColumnCount()) {
                uniqueCustomers.put(record.getValue(customerCol), 1);
            }
            if (categoryCol >= 0 && categoryCol < record.getColumnCount()) {
                uniqueCategories.put(record.getValue(categoryCol), 1);
            }
            if (monthCol >= 0 && monthCol < record.getColumnCount()) {
                uniqueMonths.put(record.getValue(monthCol), 1);
            }
        }
        
        double avgSale = totalRevenue / totalRecords;
        
        System.out.println("File: " + filePath);
        System.out.println("Total Records: " + totalRecords);
        System.out.println("Total Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Average Sale: $" + String.format("%.2f", avgSale));
        System.out.println("Min Sale: $" + String.format("%.2f", minSale));
        System.out.println("Max Sale: $" + String.format("%.2f", maxSale));
        System.out.println("Unique Customers: " + uniqueCustomers.size());
        System.out.println("Unique Categories: " + uniqueCategories.size());
        System.out.println("Unique Months: " + uniqueMonths.size());
    }
    
    public static void monthlySales() {
        System.out.println("\n=== MONTHLY SALES REPORT ===");
        
        if (monthCol < 0) {
            System.out.println("No month column detected.");
            return;
        }
        
        HashMap<String, Double> monthlySalesMap = new HashMap<>();
        HashMap<String, Integer> monthlyTransMap = new HashMap<>();
        ArrayList<String> monthOrder = new ArrayList<>();
        
        for (DataRecord record : salesData) {
            if (monthCol >= record.getColumnCount()) continue;
            String month = record.getValue(monthCol);
            if (month == null || month.trim().isEmpty()) {
                month = "Unknown Date";
            }
            double amount = getAmount(record);
            
            if (!monthlySalesMap.containsKey(month)) {
                monthlySalesMap.put(month, 0.0);
                monthlyTransMap.put(month, 0);
                monthOrder.add(month);
            }
            
            monthlySalesMap.put(month, monthlySalesMap.get(month) + amount);
            monthlyTransMap.put(month, monthlyTransMap.get(month) + 1);
        }
        
        System.out.println("Month\t\tSales\t\tTransactions");
        System.out.println("------------------------------------------------");
        
        double totalSales = 0;
        int totalTrans = 0;
        
        for (String month : monthOrder) {
            double sales = monthlySalesMap.get(month);
            int trans = monthlyTransMap.get(month);
            totalSales += sales;
            totalTrans += trans;
            
            System.out.println(month + "\t\t$" + String.format("%.2f", sales) + "\t\t" + trans);
        }
        
        System.out.println("------------------------------------------------");
        System.out.println("TOTAL\t\t$" + String.format("%.2f", totalSales) + "\t\t" + totalTrans);
    }
    
    public static void topCustomers() {
        System.out.println("\n=== TOP CUSTOMERS RANKING ===");
        
        if (customerCol < 0) {
            System.out.println("No customer column detected.");
            return;
        }
        
        HashMap<String, Double> customerSpending = new HashMap<>();
        HashMap<String, Integer> customerTrans = new HashMap<>();
        
        for (DataRecord record : salesData) {
            if (customerCol >= record.getColumnCount()) continue;
            String customer = record.getValue(customerCol);
            double amount = getAmount(record);
            
            if (!customerSpending.containsKey(customer)) {
                customerSpending.put(customer, 0.0);
                customerTrans.put(customer, 0);
            }
            
            customerSpending.put(customer, customerSpending.get(customer) + amount);
            customerTrans.put(customer, customerTrans.get(customer) + 1);
        }
        
        ArrayList<String> sortedCustomers = new ArrayList<>(customerSpending.keySet());
        for (int i = 0; i < sortedCustomers.size() - 1; i++) {
            for (int j = i + 1; j < sortedCustomers.size(); j++) {
                if (customerSpending.get(sortedCustomers.get(i)) < customerSpending.get(sortedCustomers.get(j))) {
                    String temp = sortedCustomers.get(i);
                    sortedCustomers.set(i, sortedCustomers.get(j));
                    sortedCustomers.set(j, temp);
                }
            }
        }
        
        double totalSpending = 0;
        for (Double v : customerSpending.values()) totalSpending += v;
        
        System.out.println("Rank\tCustomer\t\tTotal Spent\tTransactions");
        System.out.println("----------------------------------------------------------");
        
        int displayCount = Math.min(sortedCustomers.size(), 10);
        for (int i = 0; i < displayCount; i++) {
            String customer = sortedCustomers.get(i);
            double spent = customerSpending.get(customer);
            int trans = customerTrans.get(customer);
            
            System.out.println((i + 1) + "\t" + customer + "\t\t$" + String.format("%.2f", spent) + "\t\t" + trans);
        }
        
        System.out.println("----------------------------------------------------------");
        System.out.println("Total Customers: " + customerSpending.size() + " | Total Revenue: $" + String.format("%.2f", totalSpending));
    }
    
    public static void categoryAnalysis() {
        System.out.println("\n=== CATEGORY ANALYSIS ===");
        
        if (categoryCol < 0) {
            System.out.println("No category column detected.");
            return;
        }
        
        HashMap<String, Double> categorySales = new HashMap<>();
        HashMap<String, Integer> categoryTrans = new HashMap<>();
        
        for (DataRecord record : salesData) {
            if (categoryCol >= record.getColumnCount()) continue;
            String category = record.getValue(categoryCol);
            double amount = getAmount(record);
            
            if (!categorySales.containsKey(category)) {
                categorySales.put(category, 0.0);
                categoryTrans.put(category, 0);
            }
            
            categorySales.put(category, categorySales.get(category) + amount);
            categoryTrans.put(category, categoryTrans.get(category) + 1);
        }
        
        double totalSales = 0;
        for (Double sale : categorySales.values()) totalSales += sale;
        
        String bestCategory = "";
        String worstCategory = "";
        double maxSale = Double.MIN_VALUE;
        double minSale = Double.MAX_VALUE;
        
        for (String cat : categorySales.keySet()) {
            double sale = categorySales.get(cat);
            if (sale > maxSale) { maxSale = sale; bestCategory = cat; }
            if (sale < minSale) { minSale = sale; worstCategory = cat; }
        }
        
        System.out.println("Category\t\tRevenue\t\tShare\t\tTransactions");
        System.out.println("--------------------------------------------------------------------");
        
        int totalTrans = 0;
        for (String category : categorySales.keySet()) {
            double sales = categorySales.get(category);
            int trans = categoryTrans.get(category);
            totalTrans += trans;
            
            double percentage = (totalSales > 0) ? (sales / totalSales * 100) : 0;
            
            System.out.println(category + "\t\t$" + String.format("%.2f", sales) + "\t\t" + String.format("%.1f", percentage) + "%\t\t" + trans);
        }
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("TOTAL\t\t\t$" + String.format("%.2f", totalSales) + "\t\t100.0%\t\t" + totalTrans);
        
        System.out.println("\nInsights:");
        System.out.println("Best Performing: " + bestCategory + " ($" + String.format("%.2f", maxSale) + ")");
        System.out.println("Needs Attention: " + worstCategory + " ($" + String.format("%.2f", minSale) + ")");
    }
}
