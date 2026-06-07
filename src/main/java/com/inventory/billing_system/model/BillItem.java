package com.inventory.billing_system.model;

// Represents ONE product entry in a bill
// e.g., "USB Hub x 2 = Rs. 698"
public class BillItem {
    private int productId;
    private int quantity;
    private String productName;
    private double unitPrice;
    private double gstRate;
    private double lineTotal; // (unitPrice + GST) * quantity
    // Default constructor
    public BillItem() {}
    // Constructor used when creating a bill request from Postman
    public BillItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    // Constructor used when building the full bill response
    public BillItem(int productId, int quantity, String productName,
                    double unitPrice, double gstRate, double lineTotal) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.gstRate = gstRate;
        this.lineTotal = lineTotal;
    }
    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getGstRate() { return gstRate; }
    public void setGstRate(double gstRate) { this.gstRate = gstRate; }
    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
}