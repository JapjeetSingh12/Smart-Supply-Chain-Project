

public class ProductRetailer extends SupplyChainActor {
    WarehouseOperator warehouseOperator;

    public ProductRetailer(String name, CatalogProduct[] products, int[] quantity, int id, SalesTransaction[] paymentHistory) {
        super(name, products, quantity, id, paymentHistory);
    }

    public void sendOrderRequest(CatalogProduct item, int quantity) {
        int fulfilledQuantity = warehouseOperator.receiveOrderRequest(item, quantity);
        if (fulfilledQuantity > 0) {
            System.out.println("Order sent: " + fulfilledQuantity + " units of " + item.getName());
            updateInventory(item, fulfilledQuantity);
        } else {
            System.out.println("Order could not be fulfilled for: " + item.getName());
        }
    }

    public void updateInventory(CatalogProduct item, int quantityToAdd) {
        for (int i = 0; i < this.products.length; i++) {
            if (this.products[i].equals(item)) {
                this.quantity[i] += quantityToAdd;
                return;
            }
        }
    }

    public void addTransaction(String name, String receiver, CatalogProduct product, int amount, double price) {
        SalesTransaction transaction = new SalesTransaction(name, receiver, product, amount, price);
        for (int i = 0; i < this.paymentHistory.length; i++) {
            if (this.paymentHistory[i] == null) {
                this.paymentHistory[i] = transaction;
                break;
            }
        }
    }
}
