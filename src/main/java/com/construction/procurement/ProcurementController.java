package com.construction.procurement;

//developed by vaishnavy.s(IT25101549)

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    // ==========================================
    // SUPPLIERS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(procurementService.createSupplier(supplier));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers(@RequestParam(name = "category", required = false) String category) {
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(procurementService.getSuppliersByCategory(category));
        }
        return ResponseEntity.ok(procurementService.getAllSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(procurementService.getSupplierById(id));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable("id") Long id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(procurementService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable("id") Long id) {
        procurementService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of("message", "Supplier deleted successfully", "id", String.valueOf(id)));
    }

    // ==========================================
    // PURCHASE REQUESTS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/requests")
    public ResponseEntity<PurchaseRequest> createPurchaseRequest(@RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(procurementService.createPurchaseRequest(request));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<PurchaseRequest>> getAllPurchaseRequests(@RequestParam(name = "status", required = false) String status) {
        if (status != null && !status.trim().isEmpty()) {
            return ResponseEntity.ok(procurementService.getPurchaseRequestsByStatus(status));
        }
        return ResponseEntity.ok(procurementService.getAllPurchaseRequests());
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<PurchaseRequest> getPurchaseRequestById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(procurementService.getPurchaseRequestById(id));
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<PurchaseRequest> updatePurchaseRequest(@PathVariable("id") Long id, @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(procurementService.updatePurchaseRequest(id, request));
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<Map<String, String>> deletePurchaseRequest(@PathVariable("id") Long id) {
        procurementService.deletePurchaseRequest(id);
        return ResponseEntity.ok(Map.of("message", "Purchase request deleted successfully", "id", String.valueOf(id)));
    }

    // Strategy Pattern Demonstration Endpoint
    @PostMapping("/requests/{id}/pay")
    public ResponseEntity<Map<String, Object>> processOrderPayment(
            @PathVariable("id") Long id, 
            @RequestParam(name = "paymentMethod", defaultValue = "BANK_TRANSFER") String paymentMethod) {
        return ResponseEntity.ok(procurementService.processPurchaseOrderPayment(id, paymentMethod));
    }
}
