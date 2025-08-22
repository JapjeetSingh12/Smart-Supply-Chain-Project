
public abstract class SupplyChainActor {
    protected String name;
    protected int id;
    protected CatalogProduct[] products;
    protected int[] quantity;
    protected SalesTransaction[] paymentHistory;
    protected int distinctProductCount;

    public SupplyChainActor(String name, CatalogProduct[] products, 
                          int[] quantity, int id, SalesTransaction[] paymentHistory) {
        this.name = name;
        this.id = id;
        this.products = products;
        this.quantity = quantity;
        this.paymentHistory = paymentHistory;
        this.distinctProductCount = products.length;
    }

    public String getName() { 
        return this.name; 
    }

    public String stockUpdate() {
        StringBuilder stockDetails = new StringBuilder();
        for (int i = 0; i < products.length; i++) {
            stockDetails.append(products[i].getName())
                       .append(": ")
                       .append(quantity[i])
                       .append("\n");
        }
        return stockDetails.toString();
    }
}
