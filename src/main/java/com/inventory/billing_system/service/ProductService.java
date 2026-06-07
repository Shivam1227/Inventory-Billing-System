package com.inventory.billing_system.service;

import com.inventory.billing_system.dao.ProductDAO;
import com.inventory.billing_system.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// @Service marks this as the business logic layer
@Service
public class ProductService {
    @Autowired
    private ProductDAO productDAO;
    //  Add product
    // Validates input, creates the right subclass, saves to DB
    public Map<String, Object> addProduct(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract values from the incoming JSON request
            String name = (String) request.get("name");
            String category = ((String) request.get("category")).toUpperCase();
            double price = Double.parseDouble(request.get("price").toString());
            int stock = Integer.parseInt(request.get("stock").toString());
            int threshold = request.containsKey("threshold")
                    ? Integer.parseInt(request.get("threshold").toString())
                    : 5; // default threshold
            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Product name is required");
                return response;
            }
            if (price <= 0) {
                response.put("success", false);
                response.put("message", "Price must be greater than 0");
                return response;
            }
            if (stock < 0) {
                response.put("success", false);
                response.put("message", "Stock cannot be negative");
                return response;
            }
            // Create correct subclass based on category (Polymorphism!)
            Product product;
            switch (category) {
                case "ELECTRONICS":
                    product = new Electronics(0, name, price, stock, threshold);
                    break;
                case "GROCERY":
                    product = new Grocery(0, name, price, stock, threshold);
                    break;
                case "CLOTHING":
                    product = new Clothing(0, name, price, stock, threshold);
                    break;
                default:
                    response.put("success", false);
                    response.put("message", "Invalid category. Use: ELECTRONICS, GROCERY, CLOTHING");
                    return response;
            }
            boolean saved = productDAO.addProduct(product);
            if (saved) {
                response.put("success", true);
                response.put("message", "Product '" + name + "' added successfully");
                response.put("gstRate", product.getGstRate() + "%");
            } else {
                response.put("success", false);
                response.put("message", "Failed to save product to database");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }
    //  Get all products (with GST-included price added)
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }
    // Get single product
    public Map<String, Object> getProductById(int id) {
        Map<String, Object> response = new HashMap<>();
        Product product = productDAO.getProductById(id);
        if (product == null) {
            response.put("success", false);
            response.put("message", "No product found with ID: " + id);
        } else {
            response.put("success", true);
            response.put("id", product.getId());
            response.put("name", product.getName());
            response.put("category", product.getCategory());
            response.put("price", product.getPrice());
            response.put("gstRate", product.getGstRate() + "%");
            response.put("priceWithGst", String.format("%.2f", product.getPriceWithGst()));
            response.put("stock", product.getStock());
            response.put("threshold", product.getThreshold());
            response.put("isLowStock", product.isLowStock());
        }
        return response;
    }
    // Filter by category
    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }
    // Search by name
    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }
    // Update stock
    public Map<String, Object> updateStock(int productId, int newStock) {
        Map<String, Object> response = new HashMap<>();
        if (newStock < 0) {
            response.put("success", false);
            response.put("message", "Stock cannot be negative");
            return response;
        }
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            response.put("success", false);
            response.put("message", "Product not found with ID: " + productId);
            return response;
        }
        boolean updated = productDAO.updateStock(productId, newStock);
        if (updated) {
            response.put("success", true);
            response.put("message", "Stock updated for '" + product.getName() + "'");
            response.put("oldStock", product.getStock());
            response.put("newStock", newStock);
            if (newStock <= product.getThreshold()) {
                response.put("alert", "WARNING: Stock is at or below threshold level!");
            }
        } else {
            response.put("success", false);
            response.put("message", "Failed to update stock");
        }
        return response;
    }
    // Low stock alert
    public Map<String, Object> getLowStockAlert() {
        List<Product> lowStock = productDAO.getLowStockProducts();
        Map<String, Object> response = new HashMap<>();
        response.put("totalLowStockItems", lowStock.size());
        response.put("products", lowStock);
        if (lowStock.isEmpty()) {
            response.put("message", "All products are sufficiently stocked.");
        } else {
            response.put("message", lowStock.size() + " product(s) need restocking!");
        }
        return response;
    }
    // Delete product
    public Map<String, Object> deleteProduct(int productId) {
        Map<String, Object> response = new HashMap<>();
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            response.put("success", false);
            response.put("message", "Product not found with ID: " + productId);
            return response;
        }
        boolean deleted = productDAO.deleteProduct(productId);
        response.put("success", deleted);
        response.put("message", deleted
                ? "Product '" + product.getName() + "' deleted successfully"
                : "Failed to delete product");
        return response;
    }
}
