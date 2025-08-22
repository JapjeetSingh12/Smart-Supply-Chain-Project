import java.io.*;
import java.util.* ;

public class SupplyChainApp {
    public static void main(String[] args) throws InterruptedException {
        // Create sample products
        CatalogProduct product1 = new CatalogProduct("Laptop", "Electronics", 1);
        CatalogProduct product2 = new CatalogProduct("Phone", "Electronics", 2);
        CatalogProduct product3 = new CatalogProduct("Tablet", "Electronics", 3);

        // Create sample quantities
        int[] quantities = {10, 20, 15};

        // Create sample transactions for forecasting
        SalesTransaction[] transactions = new SalesTransaction[10];
        transactions[0] = new SalesTransaction("Order1", "Retailer1", product1, 5, 5500.0);
        transactions[1] = new SalesTransaction("Order2", "Retailer1", product2, 10, 8000.0);
        transactions[2] = new SalesTransaction("Order3", "Retailer1", product1, 7, 7700.0);
        transactions[3] = new SalesTransaction("Order4", "Retailer1", product2, 12, 9600.0);
        transactions[4] = new SalesTransaction("Order5", "Retailer1", product1, 6, 6600.0);


        // Create a retailer
        ProductRetailer retailer = new ProductRetailer("Retailer1", new CatalogProduct[]{product1, product2}, new int[]{5, 10}, 101, transactions);

        // Create a warehouse operator
        WarehouseOperator warehouseOperator = new WarehouseOperator("Warehouse1", new CatalogProduct[]{product1, product2, product3}, quantities, 201, new SalesTransaction[10]);

        // Assign the warehouse operator to the retailer
        retailer.warehouseOperator = warehouseOperator;

        // Test sending an order request
        retailer.sendOrderRequest(product1, 5);

        // Create a supplier
        ProductSupplier supplier = new ProductSupplier("Supplier1", new CatalogProduct[]{product1, product2, product3}, new int[]{50,50,50}, 301, new SalesTransaction[10]);

        // Assign suppliers to the warehouse operator
        warehouseOperator.setSuppliers(new ProductSupplier[]{supplier});

        // Test warehouse operator filling orders
        PurchaseOrder order1 = new PurchaseOrder(new PricedProduct("Laptop", "Electronics", 1, 1000.0), 5);
        warehouseOperator.addOrder(order1);
        warehouseOperator.fillOrders();


        // --- TESTING NEW FEATURES ---

        System.out.println("\n--- Testing New Features ---");

        // Test Admin Functionality
        SystemAdministrator admin = new SystemAdministrator("Admin1", 401);
        admin.addUser("Retailer", retailer);
        admin.addUser("WarehouseOperator", warehouseOperator);
        admin.addUser("ProductSupplier", supplier);

        // 1. Test Multithreaded Report Generation
        System.out.println("\n1. Testing Multithreaded Report Generation...");
        admin.generateReport();
        // Give the report a moment to be written in the background
        Thread.sleep(1000);
        System.out.println("Main thread continues execution while report is being generated.");
        System.out.println("Check for 'inventory_report.txt' in your project directory.");

        // 2. Test AI-Powered Demand Forecasting
        System.out.println("\n2. Testing AI-Powered Demand Forecasting...");
        admin.predictDemand();

        // 3. Test Barcode Scanning
        System.out.println("\n3. Testing Barcode Scanning...");
        System.out.println("Create a barcode image file named 'barcode_p1.png' with the text '1'");
        System.out.println("Attempting to add 20 units of product 1 (Laptop) via barcode scan...");
        // NOTE: You must create a barcode image file for this to work.
        // You can use an online barcode generator to create an image with the text "1".
        warehouseOperator.addStockFromBarcode("barcode_p1.gif", 20);

        System.out.println("\n--- Final Stock Levels ---");
        admin.viewAllStockLevels();


        // Important: Shutdown the admin's executor service
        admin.shutdown();
    }
}