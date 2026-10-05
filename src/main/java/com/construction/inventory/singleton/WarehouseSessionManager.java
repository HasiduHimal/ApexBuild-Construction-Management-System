package com.construction.inventory.singleton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class to manage centralized warehouse session logs and audit trails.
 * Follows the Singleton Design Pattern (SE2030 Lecture Part I, Slides 18-23).
 */
public class WarehouseSessionManager {

    // Step #2: Declare a private static instance variable
    private static WarehouseSessionManager instance;

    // Instance variables managed globally
    private final List<String> inventoryAuditLogs = new ArrayList<>();
    private int totalDisbursementsProcessed = 0;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Step #1: Make the constructor private to prevent other classes from using 'new'
    private WarehouseSessionManager() {
        logActivity("WarehouseSessionManager initialized. Centralized warehouse session started.");
    }

    // Step #3: Provide a static method to get the single instance
    public static synchronized WarehouseSessionManager getInstance() {
        if (instance == null) {
            instance = new WarehouseSessionManager();
        }
        return instance;
    }

    public synchronized void logActivity(String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        String entry = "[" + timestamp + "] " + action;
        inventoryAuditLogs.add(entry);
        System.out.println("WarehouseSingleton Log: " + entry);
    }

    public synchronized void recordDisbursement(Long materialId, double quantity) {
        totalDisbursementsProcessed++;
        logActivity("Material Issue Processed -> Material ID #" + materialId + ", Quantity: " + quantity);
    }

    public synchronized List<String> getAuditLogs() {
        return Collections.unmodifiableList(new ArrayList<>(inventoryAuditLogs));
    }

    public int getTotalDisbursementsProcessed() {
        return totalDisbursementsProcessed;
    }
}
