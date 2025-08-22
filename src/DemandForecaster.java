import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandForecaster {

    /**
     * Predicts future demand for products based on historical sales data using a Simple Moving Average (SMA).
     * @param actors A list of all supply chain actors (used to find retailers and their sales history).
     * @param windowSize The number of recent transactions to consider for the average (e.g., 3).
     * @return A map where the key is the product name and the value is the predicted weekly demand.
     */
    public static Map<String, Double> predictDemandSMA(List<SupplyChainActor> actors, int windowSize) {
        Map<String, List<Integer>> salesHistory = new HashMap<>();

        // 1. Aggregate sales data from all retailers
        for (SupplyChainActor actor : actors) {
            if (actor instanceof ProductRetailer) {
                for (SalesTransaction transaction : actor.paymentHistory) {
                    if (transaction != null) {
                        String productName = transaction.getProduct().getName();
                        int amount = transaction.getAmount();
                        salesHistory.computeIfAbsent(productName, k -> new ArrayList<>()).add(amount);
                    }
                }
            }
        }

        Map<String, Double> predictedDemand = new HashMap<>();

        // 2. Calculate the Simple Moving Average for each product
        for (Map.Entry<String, List<Integer>> entry : salesHistory.entrySet()) {
            String productName = entry.getKey();
            List<Integer> history = entry.getValue();
            double average = 0.0;

            if (history.size() > 0) {
                int start = Math.max(0, history.size() - windowSize);
                int count = 0;
                for (int i = start; i < history.size(); i++) {
                    average += history.get(i);
                    count++;
                }
                if (count > 0) {
                    average /= count;
                }
            }
            predictedDemand.put(productName, average);
        }

        return predictedDemand;
    }
}