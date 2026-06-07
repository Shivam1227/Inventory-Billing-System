package com.inventory.billing_system.model;

public class Clothing extends Product {
    public Clothing() {
        super();
    }
    public Clothing(int id, String name, double price,
                    int stock, int threshold) {
        super(id, name, "CLOTHING", price, stock, threshold);
    }
    // Clothing items attract 12% GST
    @Override
    public double getGstRate() {
        return 12.0;
    }
}