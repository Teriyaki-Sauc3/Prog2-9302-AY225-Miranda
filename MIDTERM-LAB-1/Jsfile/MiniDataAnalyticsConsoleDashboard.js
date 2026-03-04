const fs = require('fs');
const readline = require('readline');

// DataRecord class - Modular design for data records
class DataRecord {
    constructor(values, headers) {
        this.values = values;
        this.headers = headers;
    }

    getValue(index) {
        if (index >= 0 && index < this.values.length) {
            return this.values[index];
        }
        return "";
    }

    getValues() {
        return this.values;
    }

    getColumnCount() {
        return this.values.length;
    }

    getNumericValue(index) {
        if (index < 0 || index >= this.values.length) return 0;
        let val = this.values[index].replace(/[^0-9.-]/g, '');
        const parsed = parseFloat(val);
        return Number.isNaN(parsed) ? 0 : parsed;
    }
}

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// Global variables
let salesData = [];
let headers = [];
let filePath = '';
let customerCol = -1;
let categoryCol = -1;
let monthCol = -1;
let amountCol = -1;

// Validate CSV format - check if file has headers and at least one data row
function validateCSVFormat(path) {
    try {
        const content = fs.readFileSync(path, 'utf-8');
        const lines = content.split('\n');
        
        const headerLine = lines[0];
        const dataLine = lines.length > 1 ? lines[1] : null;
        
        // Check if file has at least header and one data row
        if (!headerLine || headerLine.trim() === '') {
            return false;
        }
        
        // Check if header contains commas (CSV format)
        if (!headerLine.includes(',')) {
            return false;
        }
        
        // Check if there's at least one data row
        if (!dataLine || dataLine.trim() === '') {
            return false;
        }
        
        return true;
    } catch (e) {
        return false;
    }
}

// Parse CSV line handling quoted values
function parseCSVLine(line) {
    const values = [];
    let current = '';
    let inQuotes = false;
    
    for (let i = 0; i < line.length; i++) {
        const c = line[i];
        
        if (c === '"') {
            inQuotes = !inQuotes;
        } else if (c === ',' && !inQuotes) {
            values.push(current.trim());
            current = '';
        } else {
            current += c;
        }
    }
    values.push(current.trim());
    
    return values;
}

// Load CSV dataset into memory
function loadDataset(path) {
    try {
        const content = fs.readFileSync(path, 'utf-8');
        const lines = content.split('\n');
        
        let isFirstLine = true;
        
        for (const line of lines) {
            if (line.trim() === '') continue;
            
            const values = parseCSVLine(line);
            
            if (isFirstLine) {
                headers = values;
                detectColumns(headers);
                isFirstLine = false;
            } else {
                // Create DataRecord object for each row
                const record = new DataRecord(values, headers);
                salesData.push(record);
            }
        }
        
        // Validate required columns were found
        if (amountCol === -1) {
            console.log("Error: Could not detect sales/amount column.");
            console.log("Available columns: ");
            for (let i = 0; i < headers.length; i++) {
                console.log("  " + i + ": " + headers[i]);
            }
            return false;
        }
        
        return salesData.length > 0;
    } catch (e) {
        console.log("Error reading file: " + e.message);
        return false;
    }
}

// Auto-detect column indices from headers
function detectColumns(headers) {
    for (let i = 0; i < headers.length; i++) {
        const h = headers[i].toLowerCase().trim();
        
        // Customer/Name detection
        if (h.includes('customer') || h.includes('name') || h.includes('buyer') || 
            h.includes('client') || h === 'title' || h === 'publisher') {
            if (customerCol === -1) customerCol = i;
        }
        // Category detection
        if (h.includes('category') || h.includes('product') || h.includes('type') || 
            h.includes('item') || h === 'genre' || h === 'console') {
            if (categoryCol === -1) categoryCol = i;
        }
        // Month/Date detection
        if (h.includes('month') || h.includes('date') || h.includes('period') || h.includes('year')) {
            if (monthCol === -1) monthCol = i;
        }
        // Amount/Sales detection
        if (h === 'total_sales' || h.includes('amount') || h.includes('sales') || 
            h.includes('price') || h.includes('revenue')) {
            if (amountCol === -1) amountCol = i;
        }
    }
    
    console.log("\n Column Detection Results:");
    console.log(" Customer column: " + (customerCol >= 0 ? headers[customerCol] : "Not found"));
    console.log(" Category column: " + (categoryCol >= 0 ? headers[categoryCol] : "Not found"));
    console.log(" Month column: " + (monthCol >= 0 ? headers[monthCol] : "Not found"));
    console.log(" Amount column: " + (amountCol >= 0 ? headers[amountCol] : "Not found"));
}

// Get amount value from a DataRecord
function getAmount(record) {
    return record.getNumericValue(amountCol);
}

// Display menu
function displayMenu() {
    console.log("\n-------- MAIN MENU --------");
    console.log("1 - View Dataset Summary");
    console.log("2 - Monthly Sales");
    console.log("3 - Top Customers");
    console.log("4 - Category Analysis");
    console.log("5 - Exit");
    console.log("---------------------------");
}

// View Dataset Summary
function viewDatasetSummary() {
    console.log("\n=== DATASET SUMMARY ===");
    
    const totalRecords = salesData.length;
    let totalRevenue = 0;
    let minSale = Number.MAX_VALUE;
    let maxSale = Number.MIN_VALUE;
    
    const uniqueCustomers = {};
    const uniqueCategories = {};
    const uniqueMonths = {};
    
    for (const record of salesData) {
        const amount = getAmount(record);
        totalRevenue += amount;
        if (amount < minSale) minSale = amount;
        if (amount > maxSale) maxSale = amount;
        
        if (customerCol >= 0 && customerCol < record.getColumnCount()) {
            uniqueCustomers[record.getValue(customerCol)] = 1;
        }
        if (categoryCol >= 0 && categoryCol < record.getColumnCount()) {
            uniqueCategories[record.getValue(categoryCol)] = 1;
        }
        if (monthCol >= 0 && monthCol < record.getColumnCount()) {
            uniqueMonths[record.getValue(monthCol)] = 1;
        }
    }
    
    const avgSale = totalRevenue / totalRecords;
    
    console.log("File: " + filePath);
    console.log("Total Records: " + totalRecords);
    console.log("Total Revenue: $" + totalRevenue.toFixed(2));
    console.log("Average Sale: $" + avgSale.toFixed(2));
    console.log("Min Sale: $" + minSale.toFixed(2));
    console.log("Max Sale: $" + maxSale.toFixed(2));
    console.log("Unique Customers: " + Object.keys(uniqueCustomers).length);
    console.log("Unique Categories: " + Object.keys(uniqueCategories).length);
    console.log("Unique Months: " + Object.keys(uniqueMonths).length);
}

// Monthly Sales Report
function monthlySales() {
    console.log("\n=== MONTHLY SALES REPORT ===");
    
    if (monthCol < 0) {
        console.log("No month column detected.");
        return;
    }
    
    const monthlySalesMap = {};
    const monthlyTransMap = {};
    const monthOrder = [];
    
    for (const record of salesData) {
        if (monthCol >= record.getColumnCount()) continue;
        let month = record.getValue(monthCol);
        if (!month || month.trim() === '') {
            month = 'Unknown Date';
        }
        const amount = getAmount(record);
        
        if (!monthlySalesMap[month]) {
            monthlySalesMap[month] = 0;
            monthlyTransMap[month] = 0;
            monthOrder.push(month);
        }
        
        monthlySalesMap[month] += amount;
        monthlyTransMap[month]++;
    }
    
    console.log("Month\t\tSales\t\tTransactions");
    console.log("------------------------------------------------");
    
    let totalSales = 0;
    let totalTrans = 0;
    
    for (const month of monthOrder) {
        const sales = monthlySalesMap[month];
        const trans = monthlyTransMap[month];
        totalSales += sales;
        totalTrans += trans;
        
        console.log(month + "\t\t$" + sales.toFixed(2) + "\t\t" + trans);
    }
    
    console.log("------------------------------------------------");
    console.log("TOTAL\t\t$" + totalSales.toFixed(2) + "\t\t" + totalTrans);
}

// Top Customers
function topCustomers() {
    console.log("\n=== TOP CUSTOMERS RANKING ===");
    
    if (customerCol < 0) {
        console.log("No customer column detected.");
        return;
    }
    
    const customerSpending = {};
    const customerTrans = {};
    
    for (const record of salesData) {
        if (customerCol >= record.getColumnCount()) continue;
        const customer = record.getValue(customerCol);
        const amount = getAmount(record);
        
        if (!customerSpending[customer]) {
            customerSpending[customer] = 0;
            customerTrans[customer] = 0;
        }
        
        customerSpending[customer] += amount;
        customerTrans[customer]++;
    }
    
    let sortedCustomers = Object.keys(customerSpending);
    
    // Bubble sort
    for (let i = 0; i < sortedCustomers.length - 1; i++) {
        for (let j = i + 1; j < sortedCustomers.length; j++) {
            if (customerSpending[sortedCustomers[i]] < customerSpending[sortedCustomers[j]]) {
                const temp = sortedCustomers[i];
                sortedCustomers[i] = sortedCustomers[j];
                sortedCustomers[j] = temp;
            }
        }
    }
    
    let totalSpending = 0;
    for (const v of Object.values(customerSpending)) totalSpending += v;
    
    console.log("Rank\tCustomer\t\tTotal Spent\tTransactions");
    console.log("----------------------------------------------------------");
    
    const displayCount = Math.min(sortedCustomers.length, 10);
    for (let i = 0; i < displayCount; i++) {
        const customer = sortedCustomers[i];
        const spent = customerSpending[customer];
        const trans = customerTrans[customer];
        
        console.log((i + 1) + "\t" + customer + "\t\t$" + spent.toFixed(2) + "\t\t" + trans);
    }
    
    console.log("----------------------------------------------------------");
    console.log("Total Customers: " + Object.keys(customerSpending).length + " | Total Revenue: $" + totalSpending.toFixed(2));
}

// Category Analysis
function categoryAnalysis() {
    console.log("\n=== CATEGORY ANALYSIS ===");
    
    if (categoryCol < 0) {
        console.log("No category column detected.");
        return;
    }
    
    const categorySales = {};
    const categoryTrans = {};
    
    for (const record of salesData) {
        if (categoryCol >= record.getColumnCount()) continue;
        const category = record.getValue(categoryCol);
        const amount = getAmount(record);
        
        if (!categorySales[category]) {
            categorySales[category] = 0;
            categoryTrans[category] = 0;
        }
        
        categorySales[category] += amount;
        categoryTrans[category]++;
    }
    
    let totalSales = 0;
    for (const sale of Object.values(categorySales)) totalSales += sale;
    
    let bestCategory = "";
    let worstCategory = "";
    let maxSale = Number.MIN_VALUE;
    let minSale = Number.MAX_VALUE;
    
    for (const cat of Object.keys(categorySales)) {
        const sale = categorySales[cat];
        if (sale > maxSale) { maxSale = sale; bestCategory = cat; }
        if (sale < minSale) { minSale = sale; worstCategory = cat; }
    }
    
    console.log("Category\t\tRevenue\t\tShare\t\tTransactions");
    console.log("--------------------------------------------------------------------");
    
    let totalTrans = 0;
    for (const category of Object.keys(categorySales)) {
        const sales = categorySales[category];
        const trans = categoryTrans[category];
        totalTrans += trans;
        
        const percentage = totalSales > 0 ? (sales / totalSales * 100) : 0;
        
        console.log(category + "\t\t$" + sales.toFixed(2) + "\t\t" + percentage.toFixed(1) + "%\t\t" + trans);
    }
    
    console.log("--------------------------------------------------------------------");
    console.log("TOTAL\t\t\t$" + totalSales.toFixed(2) + "\t\t100.0%\t\t" + totalTrans);
    
    console.log("\nInsights:");
    console.log("Best Performing: " + bestCategory + " ($" + maxSale.toFixed(2) + ")");
    console.log("Needs Attention: " + worstCategory + " ($" + minSale.toFixed(2) + ")");
}

// Ask for file path - loops until valid
function askFilePath() {
    rl.question("Enter dataset file path: ", function(path) {
        // Remove quotes if user copied path with quotes
        if (path.startsWith('"') && path.endsWith('"')) {
            path = path.substring(1, path.length - 1);
        }
        
        path = path.trim();
        
        // Validate: Check if path is empty
        if (path === '') {
            console.log("Error: File path cannot be empty. Please try again.");
            askFilePath();
            return;
        }
        
        // Validate: Check if file exists
        if (!fs.existsSync(path)) {
            console.log("Error: File does not exist. Please check the path and try again.");
            askFilePath();
            return;
        }
        
        const stats = fs.statSync(path);
        
        // Validate: Check if it's a file (not directory)
        if (!stats.isFile()) {
            console.log("Error: Path is not a file. Please provide a valid file path.");
            askFilePath();
            return;
        }
        
        // Validate: Check if file is CSV format
        if (!path.toLowerCase().endsWith('.csv')) {
            console.log("Error: File is not in CSV format. Please provide a .csv file.");
            askFilePath();
            return;
        }
        
        // Validate: Check if CSV has valid content
        if (!validateCSVFormat(path)) {
            console.log("Error: Invalid CSV format. File must have headers and data rows.");
            askFilePath();
            return;
        }
        
        filePath = path;
        loadDataAndStartMenu();
    });
}

// Load data and start menu
function loadDataAndStartMenu() {
    console.log("\nLoading dataset...");
    if (!loadDataset(filePath)) {
        console.log("Failed to load dataset. Exiting.");
        rl.close();
        return;
    }
    
    console.log("Dataset loaded successfully!");
    console.log("Records found: " + salesData.length);
    
    showMenu();
}

// Show menu and get user choice
function showMenu() {
    displayMenu();
    rl.question("Enter your choice: ", function(input) {
        const choice = parseInt(input);
        
        if (isNaN(choice)) {
            console.log("Invalid input! Please enter a number (1-5).");
            showMenu();
            return;
        }
        
        switch (choice) {
            case 1:
                viewDatasetSummary();
                showMenu();
                break;
            case 2:
                monthlySales();
                showMenu();
                break;
            case 3:
                topCustomers();
                showMenu();
                break;
            case 4:
                categoryAnalysis();
                showMenu();
                break;
            case 5:
                console.log("\nThanks for using the Analytics Dashboard!");
                console.log("Goodbye!\n");
                rl.close();
                break;
            default:
                console.log("\nInvalid choice! Please select 1-5.\n");
                showMenu();
        }
    });
}

// Main program start
console.log("========================================");
console.log("  MINI DATA ANALYTICS CONSOLE DASHBOARD");
console.log("========================================");
console.log();

askFilePath();
