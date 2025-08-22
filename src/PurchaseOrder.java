
public class PurchaseOrder {
    private int quantity;
    private double totalPayout;
    private PricedProduct product;

    public PurchaseOrder(PricedProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPayout = quantity * product.getPrice();
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getTotalPayout() {
        return this.totalPayout;
    }

    public PricedProduct getProduct() {
        return this.product;
    }
}
