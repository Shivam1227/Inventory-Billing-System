package com.inventory.billing_system.model;
// Abstract class = cannot create 'new Product()' directly
// Forces all subclasses to implement getGstRate()
public abstract class Product {
    // Private fields = Encapsulation (data hiding)
    private int id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int threshold; // low-stock alert level
    // Default constructor (needed by Spring/Jackson for JSON)
    public Product() {}
    // Parameterized constructor
    public Product(int id, String name, String category,
                   double price, int stock, int threshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
    }
    // Abstract method
// Every subclass MUST implement this (Polymorphism)
// Electronics → 18%, Grocery → 5%, Clothing → 12%
    public abstract double getGstRate();
    // Concrete method (same for all subclasses)
// Calculates price including GST
    public double getPriceWithGst() {
        return this.price + (this.price * getGstRate() / 100);
    }
    // Returns true if stock is at or below the warning threshold
    public boolean isLowStock() {
        return this.stock <= this.threshold;
    }
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    // toString() for debugging — prints a summary of the product
    @Override
    public String toString() {
        return String.format("[%s] %s | Price: Rs.%.2f | Stock: %d",
                category, name, price, stock);
    }
}