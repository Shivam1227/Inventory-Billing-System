package com.inventory.billing_system.model;

public class Grocery extends Product {
    public Grocery() {
        super();
    }
    public Grocery(int id, String name, double price,
                   int stock, int threshold) {
        super(id, name, "GROCERY", price, stock, threshold);
    }
    // Grocery items attract only 5% GST
    @Override
    public double getGstRate() {
        return 5.0;
    }
}
