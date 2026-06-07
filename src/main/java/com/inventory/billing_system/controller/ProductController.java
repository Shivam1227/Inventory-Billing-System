package com.inventory.billing_system.controller;

import com.inventory.billing_system.model.Product;
import com.inventory.billing_system.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
// @RestController = handles HTTP requests and returns JSON automatically
// @RequestMapping = all endpoints in this class start with /api/products
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    // GET /api/products
    // Returns ALL products in the database
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    // GET /api/products/{id}
    // Returns ONE product by its ID, with GST details
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable int id) {
        Map<String, Object> result = productService.getProductById(id);
        return ResponseEntity.ok(result);
    }
    // GET /api/products/category/{category}
    // Returns all products in a specific category
    // Example: GET /api/products/category/ELECTRONICS
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    // GET /api/products/search?keyword=laptop
    // Returns products matching the keyword in their name
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
    // GET /api/products/low-stock
    // Returns all products that are below their threshold
    @GetMapping("/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStockAlert() {
        Map<String, Object> result = productService.getLowStockAlert();
        return ResponseEntity.ok(result);
    }
    // POST /api/products
    // Adds a new product to the database
    // Request body (JSON): { "name": "...", "category": "...",
    // "price": 0.0, "stock": 0 }
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestBody Map<String, Object> request) {
        Map<String, Object> result = productService.addProduct(request);
        return ResponseEntity.ok(result);
    }
    // PUT /api/products/{id}/stock
    // Updates stock quantity for a product
    // Request body (JSON): { "stock": 100 }
    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable int id,
            @RequestBody Map<String, Object> request) {
        int newStock = Integer.parseInt(request.get("stock").toString());
        Map<String, Object> result = productService.updateStock(id, newStock);
        return ResponseEntity.ok(result);
    }
    // DELETE /api/products/{id}
    // Removes a product from the database
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        Map<String, Object> result = productService.deleteProduct(id);
        return ResponseEntity.ok(result);
    }
}
