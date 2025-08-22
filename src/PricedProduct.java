

public class PricedProduct extends CatalogProduct {
    private double price;

    public PricedProduct(String name, String category, int id, double price) {
        super(name, category, id);
        this.price = price;
    }

    public PricedProduct(String name, int id, double price) {
        super(name, id);
        this.price = price;
    }

    public double getPrice() { 
        return this.price; 
    }
    
    public String getProductName() { 
        return this.getName(); 
    }
}
