import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WarehouseOperator extends SupplyChainActor {
    private CatalogProduct[] targetProducts;
    private PurchaseOrder[] orders;
    private ProductSupplier[] suppliers;
    private double totalCost = 0.0;
    private double totalRevenue = 0.0;

    public WarehouseOperator(String name, CatalogProduct[] products, int[] quantity, int id, SalesTransaction[] paymentHistory) {
        super(name, products, quantity, id, paymentHistory);
        this.targetProducts = products;
        this.suppliers = new ProductSupplier[0];
        this.orders = new PurchaseOrder[0];
    }

    public CatalogProduct[] getTargetProducts() { return this.targetProducts; }
    public PurchaseOrder[] getOrders() { return this.orders; }
    public ProductSupplier[] getSuppliers() { return this.suppliers; }
    public void setSuppliers(ProductSupplier[] suppliers) { this.suppliers = suppliers; }
    public int[] getQuantities() { return this.quantity; }

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
            "Order from Retailer",
            "Warehouse",
            item,
            quantity,
            currQuantity * 100.0 // Example price logic
        );
        // Logic to add the transaction to paymentHistory would go here
        return quantity;
    }

    public void addOrder(PurchaseOrder item) {
        int len = (this.orders == null) ? 0 : this.orders.length;
        PurchaseOrder[] newOrder = new PurchaseOrder[len + 1];
        for (int i = 0; i < len; i++) {
            newOrder[i] = this.orders[i];
        }
        newOrder[len] = item;
        this.orders = newOrder;
    }

    public void receiveRetailerRequest(PurchaseOrder... requests) {
        for (PurchaseOrder p : requests) {
            this.addOrder(p);
        }
    }

    public void addStock(CatalogProduct item, int quantity) {
        int prodind = 0;
        for (prodind = 0; prodind < this.distinctProductCount; prodind++) {
            if (this.products[prodind].equals(item)) {
                break;
            }
        }
        int len = this.products.length;
        if (prodind == len) return;
        this.quantity[prodind] += quantity;
    }
    
    public void addStockFromBarcode(String barcodeImagePath, int quantityToAdd) {
        try {
            File barcodeFile = new File(barcodeImagePath);
            // This line might fail if the file doesn't exist.
            if (!barcodeFile.exists()) {
                System.err.println("Error: Barcode image file not found at '" + barcodeImagePath + "'");
                System.err.println("Please create a barcode image named 'barcode_p1.png' with the text '1' and place it in your project's root directory.");
                return;
            }
            BufferedImage bufferedImage = ImageIO.read(barcodeFile);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            
            Result result = new MultiFormatReader().decode(bitmap);
            String barcodeText = result.getText();
            System.out.println("Barcode Scanned. Decoded text: " + barcodeText);

            int productId = Integer.parseInt(barcodeText);

            boolean productFound = false;
            for (int i = 0; i < this.products.length; i++) {
                if (this.products[i].getId() == productId) {
                    this.quantity[i] += quantityToAdd;
                    System.out.println("Stock updated for " + this.products[i].getName() + ". New quantity: " + this.quantity[i]);
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                System.out.println("Error: Product with ID " + productId + " not found in warehouse inventory.");
            }

        // CORRECTED CATCH BLOCK
        } catch (IOException | NotFoundException | NumberFormatException e) {
            System.err.println("Error processing barcode: " + e.getMessage());
        }
    }

    public void fillOrders() {
        if (this.suppliers == null || this.suppliers.length == 0) {
            System.out.println("No suppliers available to fill orders.");
            return;
        }
        for (PurchaseOrder o : this.orders) {
            PricedProduct targetProduct = o.getProduct();
            int targetQuantity = o.getQuantity();
            while (targetQuantity > 0) {
                ProductSupplier potentialSupplier = null;
                int minPrice = Integer.MAX_VALUE;
                for (ProductSupplier s : this.suppliers) {
                    int supplierPrice = s.getPrice(targetProduct);
                    if (supplierPrice == -1 || s.getQuantity(targetProduct) <= 0) continue;
                    if (supplierPrice < minPrice) {
                        potentialSupplier = s;
                        minPrice = supplierPrice;
                    }
                }
                if (potentialSupplier == null) {
                    System.out.println("Unable to fulfill order for " + targetProduct.getProductName());
                    break;
                }
                int currQuantity = potentialSupplier.getQuantity(targetProduct);
                int quantityToReduce = Math.min(currQuantity, targetQuantity);
                potentialSupplier.reduceStock(targetProduct, quantityToReduce);
                targetQuantity -= quantityToReduce;
                this.totalCost += quantityToReduce * minPrice;
                this.totalRevenue += o.getQuantity() * targetProduct.getPrice();
            }
        }
    }

    public double calculateProfitOrLoss() {
        return this.totalRevenue - this.totalCost;
    }

    public void displayProfitOrLoss() {
        double profitOrLoss = calculateProfitOrLoss();
        if (profitOrLoss > 0) {
            System.out.println("Profit: $" + profitOrLoss);
        } else if (profitOrLoss < 0) {
            System.out.println("Loss: $" + Math.abs(profitOrLoss));
        } else {
            System.out.println("No profit or loss.");
        }
    }
}
