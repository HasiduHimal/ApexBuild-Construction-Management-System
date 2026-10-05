package com.construction.inventory;

// Developed & Verified by Bandara R.A.H.G.D (IT25101722)

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ==========================================
    // MATERIALS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/materials")
    public ResponseEntity<Material> createMaterial(@RequestBody Material material) {
        return ResponseEntity.ok(inventoryService.createMaterial(material));
    }

    @GetMapping("/materials")
    public ResponseEntity<List<Material>> getAllMaterials(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "activeOnly", required = false) Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(inventoryService.getActiveMaterials());
        }
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(inventoryService.getMaterialsByCategory(category));
        }
        return ResponseEntity.ok(inventoryService.getAllMaterials());
    }

    @PostMapping("/materials/{id}/adjust-stock")
    public ResponseEntity<Material> adjustStock(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> payload) {
        int quantity = Integer.parseInt(payload.get("quantity").toString());
        String reason = (String) payload.getOrDefault("reason", "Other");
        String notes = (String) payload.getOrDefault("notes", "");
        return ResponseEntity.ok(inventoryService.adjustStockRemoval(id, quantity, reason, notes));
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(inventoryService.getMaterialById(id));
    }

    @PutMapping("/materials/{id}")
    public ResponseEntity<Material> updateMaterial(@PathVariable("id") Long id, @RequestBody Material material) {
        return ResponseEntity.ok(inventoryService.updateMaterial(id, material));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<Map<String, String>> deleteMaterial(@PathVariable("id") Long id) {
        inventoryService.deleteMaterial(id);
        return ResponseEntity.ok(Map.of("message", "Material deleted successfully", "id", String.valueOf(id)));
    }

    // ==========================================
    // MATERIAL ISSUES REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/issues")
    public ResponseEntity<MaterialIssue> issueMaterial(@RequestBody MaterialIssue issue) {
        return ResponseEntity.ok(inventoryService.issueMaterial(issue));
    }

    @GetMapping("/issues")
    public ResponseEntity<List<MaterialIssue>> getAllMaterialIssues(@RequestParam(name = "projectId", required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(inventoryService.getMaterialIssuesByProject(projectId));
        }
        return ResponseEntity.ok(inventoryService.getAllMaterialIssues());
    }

    @PutMapping("/issues/{id}")
    public ResponseEntity<MaterialIssue> updateMaterialIssue(@PathVariable("id") Long id, @RequestBody MaterialIssue issue) {
        return ResponseEntity.ok(inventoryService.updateMaterialIssue(id, issue));
    }

    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Map<String, String>> deleteMaterialIssue(@PathVariable("id") Long id) {
        inventoryService.deleteMaterialIssue(id);
        return ResponseEntity.ok(Map.of("message", "Material issue cancelled and stock restored successfully", "id", String.valueOf(id)));
    }

    // Singleton Pattern Demonstration Endpoint
    @GetMapping("/audit-logs")
    public ResponseEntity<List<String>> getWarehouseAuditLogs() {
        return ResponseEntity.ok(inventoryService.getWarehouseAuditLogs());
    }
}
