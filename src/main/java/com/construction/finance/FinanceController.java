package com.construction.finance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    // ==========================================
    // EXPENSES REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(financeService.createExpense(expense));
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses(@RequestParam(name = "projectId", required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(financeService.getExpensesByProject(projectId));
        }
        return ResponseEntity.ok(financeService.getAllExpenses());
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(financeService.getExpenseById(id));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable("id") Long id, @RequestBody Expense expense) {
        return ResponseEntity.ok(financeService.updateExpense(id, expense));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable("id") Long id) {
        financeService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully", "id", String.valueOf(id)));
    }

    // ==========================================
    // QUOTATIONS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/quotations")
    public ResponseEntity<Quotation> createQuotation(@RequestBody Quotation quotation) {
        return ResponseEntity.ok(financeService.createQuotation(quotation));
    }

    @GetMapping("/quotations")
    public ResponseEntity<List<Quotation>> getAllQuotations(@RequestParam(name = "clientEmail", required = false) String clientEmail) {
        if (clientEmail != null && !clientEmail.trim().isEmpty()) {
            return ResponseEntity.ok(financeService.getQuotationsByClientEmail(clientEmail));
        }
        return ResponseEntity.ok(financeService.getAllQuotations());
    }

    @GetMapping("/quotations/{id}")
    public ResponseEntity<Quotation> getQuotationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(financeService.getQuotationById(id));
    }

    @PutMapping("/quotations/{id}")
    public ResponseEntity<Quotation> updateQuotation(@PathVariable("id") Long id, @RequestBody Quotation quotation) {
        return ResponseEntity.ok(financeService.updateQuotation(id, quotation));
    }

    @DeleteMapping("/quotations/{id}")
    public ResponseEntity<Map<String, String>> deleteQuotation(@PathVariable("id") Long id) {
        financeService.deleteQuotation(id);
        return ResponseEntity.ok(Map.of("message", "Quotation deleted successfully", "id", String.valueOf(id)));
    }

    // Decorator Pattern Demonstration Endpoint
    @GetMapping("/quotations/{id}/custom-estimate")
    public ResponseEntity<Map<String, Object>> getCustomQuotationEstimate(
            @PathVariable("id") Long id,
            @RequestParam(name = "architecturalPlan", defaultValue = "false") boolean architecturalPlan,
            @RequestParam(name = "luxuryFinishes", defaultValue = "false") boolean luxuryFinishes,
            @RequestParam(name = "governmentTax", defaultValue = "true") boolean governmentTax) {
        return ResponseEntity.ok(financeService.calculateCustomQuotationEstimate(id, architecturalPlan, luxuryFinishes, governmentTax));
    }
}
