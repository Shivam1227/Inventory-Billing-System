package com.inventory.billing_system.model;
// Electronics extends Product — inherits all fields and methods
// Only adds its specific GST rate: 18%
public class Electronics extends Product {
    public Electronics() {
        super(); // calls parent default constructor
    }
    public Electronics(int id, String name, double price,
                       int stock, int threshold) {
// Call parent constructor — category is always "ELECTRONICS"
        super(id, name, "ELECTRONICS", price, stock, threshold);
    }
    // Implementing the abstract method from Product
// Electronics attract 18% GST in India
    @Override
    public double getGstRate() {
        return 18.0;
    }
}