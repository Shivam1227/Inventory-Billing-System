package com.inventory.billing_system.controller;

import com.inventory.billing_system.model.BillItem;
import com.inventory.billing_system.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/billing")
public class BillingController {
    @Autowired
    private BillingService billingService;
    // POST /api/billing/generate
    // Generates a complete bill for a list of items
    //
    // Request body example:
    // [
    // { "productId": 1, "quantity": 2 },
    // { "productId": 5, "quantity": 3 }
    // ]
    //
    // Response: bill number, item details with GST, grand total,
    // and path to the generated .txt file
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateBill(
            @RequestBody List<BillItem> items) {
        Map<String, Object> result = billingService.generateBill(items);
        return ResponseEntity.ok(result);
    }
}