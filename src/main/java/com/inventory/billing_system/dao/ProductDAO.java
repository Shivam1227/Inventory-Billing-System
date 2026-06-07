package com.inventory.billing_system.dao;

import com.inventory.billing_system.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// @Repository marks this as the data-access layer
@Repository
public class ProductDAO {
    @Autowired
    private DatabaseConnection dbConnection;

    // HELPER: converts a ResultSet row into the correct
    // Product subclass based on 'category' column

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        double price = rs.getDouble("price");
        int stock = rs.getInt("stock");
        int threshold = rs.getInt("threshold");

        // Polymorphism: return the correct subclass
        switch (category) {
            case "ELECTRONICS": return new Electronics(id, name, price, stock, threshold);
            case "GROCERY": return new Grocery (id, name, price, stock, threshold);
            case "CLOTHING": return new Clothing (id, name, price, stock, threshold);
            default: throw new IllegalArgumentException("Unknown category: " + category);
        }
    }
    // CREATE: Add a new product to the database
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, price, stock, threshold) " +
                "VALUES (?, ?, ?, ?, ?)";
        // try-with-resources: automatically closes Connection when done
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt (4, product.getStock());
            ps.setInt (5, product.getThreshold());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // true if insert succeeded
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }
    // READ ALL: Get every product in the database
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products ORDER BY category, name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }
    // READ ONE: Find a product by its ID
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
        }
        return null; // product not found
    }
    // READ BY CATEGORY: Filter products by category
    public List<Product> getProductsByCategory(String category) {
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.toUpperCase());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error filtering products: " + e.getMessage());
        }
        return products;
    }
    // SEARCH: Find products by name (partial match)
    public List<Product> searchProducts(String keyword) {
    // LIKE '%keyword%' matches anywhere in the name
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return products;
    }
    // UPDATE STOCK: Change how many units are available
    public boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }
    // REDUCE STOCK: Subtract sold quantity from stock
    // Used during billing
    public boolean reduceStock(int productId, int quantitySold) {
        String sql = "UPDATE products SET stock = stock - ? " +
                "WHERE id = ? AND stock >= ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantitySold);
            ps.setInt(2, productId);
            ps.setInt(3, quantitySold); // safety: won't go negative
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error reducing stock: " + e.getMessage());
            return false;
        }
    }
    // LOW STOCK ALERT: Find all products below their threshold
    public List<Product> getLowStockProducts() {
        String sql = "SELECT * FROM products WHERE stock <= threshold ORDER BY stock ASC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low stock products: " + e.getMessage());
        }
        return products;
    }
    // DELETE: Remove a product by ID
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }
    // SAVE BILL: Record a completed bill in the database
    public int saveBill(String billNumber, double subtotal,
                        double gstAmount, double total, String filePath) {
        String sql = "INSERT INTO bills (bill_number, subtotal, gst_amount, total, file_path) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, billNumber);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, gstAmount);
            ps.setDouble(4, total);
            ps.setString(5, filePath);
            ps.executeUpdate();
            // Get the auto-generated bill ID
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
        }
        return -1; // error
    }
    // SAVE BILL ITEM: Record each line item of a bill
    public void saveBillItem(int billId, int productId, int quantity,
                             double unitPrice, double gstRate, double lineTotal) {
        String sql = "INSERT INTO bill_items " +
                "(bill_id, product_id, quantity, unit_price, gst_rate, line_total) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt (1, billId);
            ps.setInt (2, productId);
            ps.setInt (3, quantity);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, gstRate);
            ps.setDouble(6, lineTotal);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving bill item: " + e.getMessage());
        }
    }
}