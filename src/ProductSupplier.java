

import java.util.Random;

public class ProductSupplier extends SupplyChainActor {

    private Random rand = new Random();
    private PricedProduct[] productsOnMarket;

    public ProductSupplier(String name, CatalogProduct[] products, int[] quantity, int id, SalesTransaction[] paymentHistory) {
        super(name, products, quantity, id, paymentHistory);
        this.productsOnMarket = new PricedProduct[this.distinctProductCount];
        createProductsOnMarket();
    }

    public PricedProduct[] getProductsOnMarket() {
        return this.productsOnMarket;
    }

    public Random getRand() {
        return this.rand;
    }

    private void createProductsOnMarket() {
        for (int i = 0; i < this.distinctProductCount; i++) {
            CatalogProduct product = this.products[i];
            double price;
            switch (product.getCategory()) {
                case "Food":
                    price = 10 + rand.nextInt(21);
                    break;
                case "Electronics":
                    price = 800 + rand.nextInt(201);
                    break;
                case "MakeUp":
                    price = 20 + rand.nextInt(51);
                    break;
                case "Miscellaneous":
                    price = 5 + rand.nextInt(16);
                    break;
                case "MiscellaneousExpensive":
                    price = 200 + rand.nextInt(301);
                    break;
                default:
                    price = 50;
                    break;
            }
            this.productsOnMarket[i] = new PricedProduct(
                product.getName(),
                product.getCategory(),
                product.getId(),
                price
            );
        }
    }

    public int receiveOrderRequest(CatalogProduct item, int quantity) {
        boolean found = false;
        int productInd = 0;
        for (productInd = 0; productInd < this.distinctProductCount; productInd++) {
            if (this.products[productInd].equals(item)) {
                found = true;
                break;
            }
        }
        if (!found) return 0;
        int currQuantity = this.quantity[productInd];
        if (currQuantity < quantity) {
            this.quantity[productInd] = 0;
            return currQuantity;
        }
        currQuantity -= quantity;
        this.quantity[productInd] = currQuantity;
        SalesTransaction transaction = new SalesTransaction(
            "Order from Supplier",
            "Warehouse",
            item,
            quantity,
            currQuantity * getPrice(item)
        );
        // Optionally add transaction to paymentHistory here
        return quantity;
    }

    public int getPrice(CatalogProduct item) {
        int id = item.getId();
        for (int i = 0; i < this.distinctProductCount; i++) {
            if (productsOnMarket[i] != null && productsOnMarket[i].getId() == id) {
                return (int) productsOnMarket[i].getPrice();
            }
        }
        return -1;
    }

    public int getPrice(PricedProduct item) {
        int id = item.getId();
        for (int i = 0; i < this.distinctProductCount; i++) {
            if (productsOnMarket[i] != null && productsOnMarket[i].getId() == id) {
                return (int) productsOnMarket[i].getPrice();
            }
        }
        return -1;
    }

    public int getQuantity(CatalogProduct item) {
        int id = item.getId();
        for (int i = 0; i < this.distinctProductCount; i++) {
            if (productsOnMarket[i] != null && productsOnMarket[i].getId() == id) {
                return this.quantity[i];
            }
        }
        return 0;
    }

    public int getQuantity(PricedProduct item) {
        int id = item.getId();
        for (int i = 0; i < this.distinctProductCount; i++) {
            if (productsOnMarket[i] != null && productsOnMarket[i].getId() == id) {
                return this.quantity[i];
            }
        }
        return 0;
    }

    public void reduceStock(CatalogProduct item, int quantity) {
        int productInd = 0;
        for (productInd = 0; productInd < this.distinctProductCount; productInd++) {
            if (this.products[productInd].equals(item)) {
                break;
            }
        }
        if (productInd == this.distinctProductCount) return;
        if (this.quantity[productInd] < quantity) return;
        this.quantity[productInd] -= quantity;
    }

    public void reduceStock(PricedProduct item, int quantity) {
        int productInd = 0;
        for (productInd = 0; productInd < this.distinctProductCount; productInd++) {
            if (productsOnMarket[productInd] != null && productsOnMarket[productInd].getId() == item.getId()) {
                break;
            }
        }
        if (productInd == this.distinctProductCount) return;
        if (this.quantity[productInd] < quantity) return;
        this.quantity[productInd] -= quantity;
    }
}
