import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface SystemMonitor {
    void generateReport();
    void alertLowStock();
}

public class SystemAdministrator implements SystemMonitor {
    String name;
    int id;
    Map<String, List<SupplyChainActor>> usersByRole = new HashMap<>();
    private final ExecutorService reportExecutor = Executors.newSingleThreadExecutor();


    public SystemAdministrator(String name, int id) {
        this.name = name;
        this.id = id;
        usersByRole.put("Retailer", new ArrayList<>());
        usersByRole.put("WarehouseOperator", new ArrayList<>());
        usersByRole.put("ProductSupplier", new ArrayList<>());
    }

    public void addUser(String role, SupplyChainActor user) {
        usersByRole.get(role).add(user);
    }

    public void addUser(String role, SupplyChainActor... users) {
        usersByRole.get(role).addAll(Arrays.asList(users));
    }

    public void viewAllStockLevels() {
        for (String role : usersByRole.keySet()) {
            System.out.println("-- " + role + " --");
            for (SupplyChainActor p : usersByRole.get(role)) {
                System.out.println(p.getName() + "'s Stock:\n" + p.stockUpdate());
            }
        }
    }

    public void viewStockGraph() {
        for (String role : usersByRole.keySet()) {
            System.out.println("-- " + role + " --");
            for (SupplyChainActor p : usersByRole.get(role)) {
                System.out.println(p.getName() + "'s Stock:");
                for (int i = 0; i < p.products.length; i++) {
                    System.out.printf("%-15s | %s%n",
                        p.products[i].getName(),
                        "*".repeat(p.quantity[i]));
                }
            }
        }
    }

    /**
     * Generates a system-wide inventory report in a separate thread to avoid blocking.
     */
    @Override
    public void generateReport() {
        System.out.println("Submitting report generation task to worker thread...");

        Runnable reportTask = () -> {
            try (FileWriter fw = new FileWriter("inventory_report.txt")) {
                fw.write("--- System Inventory Report ---\n\n");
                for (String role : usersByRole.keySet()) {
                    fw.write("Role: " + role + "\n");
                    for (SupplyChainActor p : usersByRole.get(role)) {
                        fw.write("User: " + p.getName() + "\n" + p.stockUpdate() + "\n");
                    }
                }
                AdminUtils.log("Inventory report generated successfully.");
            } catch (IOException e) {
                System.err.println("Error writing report: " + e.getMessage());
                AdminUtils.log("Error writing report: " + e.getMessage());
            }
        };

        reportExecutor.submit(reportTask);
    }

    @Override
    public void alertLowStock() {
        for (String role : usersByRole.keySet()) {
            for (SupplyChainActor p : usersByRole.get(role)) {
                for (int i = 0; i < p.products.length; i++) {
                    if (p.quantity[i] < 5) { // Low stock threshold is 5
                        System.out.println("LOW STOCK ALERT: " +
                            p.products[i].getName() + " (" + p.quantity[i] + " units) for " + p.getName());
                    }
                }
            }
        }
    }

    /**
     * Shuts down the executor service gracefully. Should be called when the application closes.
     */
    public void shutdown() {
        reportExecutor.shutdown();
    }


    static class AdminUtils {
        public static void log(String message) {
            try (FileWriter fw = new FileWriter("admin_log.txt", true)) {
                fw.write(new java.util.Date() + ": " + message + "\n");
            } catch (IOException e) {
                System.err.println("Logging failed: " + e.getMessage());
            }
        }
    }

    /**
     * Predicts future demand by calling the DemandForecaster and prints the results.
     */
    public void predictDemand() {
        System.out.println("\nPredicting future demand using sales history (Simple Moving Average)...");
        List<SupplyChainActor> allActors = new ArrayList<>();
        for(List<SupplyChainActor> actorList : usersByRole.values()){
            allActors.addAll(actorList);
        }

        // Use a window size of the last 3 sales for the average
        Map<String, Double> predictions = DemandForecaster.predictDemandSMA(allActors, 3);

        if(predictions.isEmpty()){
            System.out.println("Not enough sales data to make a prediction.");
            return;
        }

        System.out.println("Predicted weekly demand:");
        for(Map.Entry<String, Double> prediction : predictions.entrySet()){
            System.out.printf("- %s: %.2f units/week\n", prediction.getKey(), prediction.getValue());
        }
    }
}