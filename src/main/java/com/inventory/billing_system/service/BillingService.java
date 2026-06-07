package com.inventory.billing_system.service;

import com.inventory.billing_system.dao.ProductDAO;
import com.inventory.billing_system.model.BillItem;
import com.inventory.billing_system.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class BillingService {
    @Autowired
    private ProductDAO productDAO;
    // Reads the bills directory from application.properties
    @Value("${app.bills.directory}")
    private String billsDirectory;
    // GENERATE BILL
    // Input: list of { productId, quantity }
    // Output: complete bill with totals, GST, file path
    public Map<String, Object> generateBill(List<BillItem> requestItems) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (requestItems == null || requestItems.isEmpty()) {
            response.put("success", false);
            response.put("message", "No items provided for billing");
            return response;
        }
        // Step 1: Validate all items and calculate totals
        List<BillItem> processedItems = new ArrayList<>();
        double subtotal = 0.0;
        double totalGst = 0.0;
        for (BillItem requestItem : requestItems) {
            Product product = productDAO.getProductById(requestItem.getProductId());
            // Check product exists
            if (product == null) {
                response.put("success", false);
                response.put("message", "Product not found: ID " + requestItem.getProductId());
                return response;
            }
            // Check sufficient stock
            if (product.getStock() < requestItem.getQuantity()) {
                response.put("success", false);
                response.put("message", "Insufficient stock for '" + product.getName()
                        + "'. Available: " + product.getStock()
                        + ", Requested: " + requestItem.getQuantity());
                return response;
            }
            // Calculate line total with GST
            double basePrice = product.getPrice() * requestItem.getQuantity();
            double gstAmount = basePrice * product.getGstRate() / 100;
            double lineTotal = Math.round((basePrice + gstAmount) * 100.0) / 100.0;
            subtotal += basePrice;
            totalGst += gstAmount;
            // Create a fully populated BillItem
            BillItem processed = new BillItem(
                    product.getId(),
                    requestItem.getQuantity(),
                    product.getName(),
                    product.getPrice(),
                    product.getGstRate(),
                    lineTotal
            );
            processedItems.add(processed);
        }
        double grandTotal = subtotal + totalGst;
        // Step 2: Deduct stock for all items
        for (BillItem item : processedItems) {
            productDAO.reduceStock(item.getProductId(), item.getQuantity());
        }
        // Step 3: Generate bill number and save to DB
        String billNumber = generateBillNumber();
        String filePath = writeBillToFile(billNumber, processedItems,
                subtotal, totalGst, grandTotal);
        int billId = productDAO.saveBill(billNumber, subtotal, totalGst,
                grandTotal, filePath);
        // Save each line item to DB
        for (BillItem item : processedItems) {
            productDAO.saveBillItem(billId, item.getProductId(), item.getQuantity(),
                    item.getUnitPrice(), item.getGstRate(), item.getLineTotal());
        }
        // Step 4: Build the response
        response.put("success", true);
        response.put("billId", billId);
        response.put("billNumber", billNumber);
        response.put("items", processedItems);
        response.put("subtotal", String.format("%.2f", subtotal));
        response.put("totalGst", String.format("%.2f", totalGst));
        response.put("grandTotal", String.format("%.2f", grandTotal));
        response.put("billFile", filePath);
        response.put("message", "Bill generated successfully!");
        return response;
    }
    // GENERATE BILL NUMBER
    // Format: B-20240528-143022
    private String generateBillNumber() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return "B-" + LocalDateTime.now().format(fmt);
    }
    // WRITE BILL TO .TXT FILE
    // Uses Java File I/O — writes a formatted receipt
    private String writeBillToFile(String billNumber, List<BillItem> items,
                                   double subtotal, double gstAmount, double total) {
        try {
            // Create bills directory if it doesn't exist
            Files.createDirectories(Paths.get(billsDirectory));
            String fileName = billsDirectory + billNumber + ".txt";
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
            String dateTime = LocalDateTime.now().format(fmt);
            try (FileWriter fw = new FileWriter(fileName)) {
                fw.write("============================================================\n");
                fw.write("              INVENTORY & BILLING SYSTEM\n");
                fw.write("                  All In One Store\n");
                fw.write("============================================================\n");

                fw.write(String.format("Bill No : %s%n", billNumber));
                fw.write(String.format("Date    : %s%n", dateTime));

                fw.write("------------------------------------------------------------\n");
                fw.write(String.format("%-25s %-5s %-10s %-10s%n",
                        "Product", "Qty", "Price", "Total"));
                fw.write("------------------------------------------------------------\n");

                for (BillItem item : items) {

                    double gst = item.getLineTotal()
                            - (item.getUnitPrice() * item.getQuantity());

                    fw.write(String.format("%-25s %-5d %-10.2f %-10.2f%n",
                            item.getProductName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getLineTotal()));

                    fw.write(String.format("   GST @ %.0f%% : %.2f%n",
                            item.getGstRate(),
                            gst));
                }

                fw.write("------------------------------------------------------------\n");

                fw.write(String.format("%-40s %10.2f%n",
                        "Subtotal (Excl. GST)", subtotal));

                fw.write(String.format("%-40s %10.2f%n",
                        "Total GST", gstAmount));

                fw.write("============================================================\n");

                fw.write(String.format("%-40s %10.2f%n",
                        "GRAND TOTAL", total));

                fw.write("============================================================\n");
                fw.write("         Thank You For Your Purchase!\n");
                fw.write("============================================================\n");
            }
            return fileName;
        } catch (IOException e) {
            System.err.println("Error writing bill file: " + e.getMessage());
            return "Error creating bill file";
        }
    }
}