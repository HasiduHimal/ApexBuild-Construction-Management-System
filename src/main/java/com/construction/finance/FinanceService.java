package com.construction.finance;
//developed by Sehansa Pahanmi (IT25103433)

import com.construction.finance.decorator.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FinanceService {

    private final ExpenseRepository expenseRepository;
    private final QuotationRepository quotationRepository;

    public FinanceService(ExpenseRepository expenseRepository, QuotationRepository quotationRepository) {
        this.expenseRepository = expenseRepository;
        this.quotationRepository = quotationRepository;
    }

    // ==========================================
    // EXPENSES CRUD
    // ==========================================

    public Expense createExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount() <= 0.0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()) {
            return expense.get();
        } else {
            throw new RuntimeException("Expense not found with ID: " + id);
        }
    }

    public List<Expense> getExpensesByProject(Long projectId) {
        return expenseRepository.findByProjectId(projectId);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        if (updatedExpense.getAmount() == null || updatedExpense.getAmount() <= 0.0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        Expense expense = getExpenseById(id);
        expense.setExpenseTitle(updatedExpense.getExpenseTitle());
        expense.setCategory(updatedExpense.getCategory());
        expense.setAmount(updatedExpense.getAmount());
        expense.setExpenseDate(updatedExpense.getExpenseDate());
        expense.setDescription(updatedExpense.getDescription());
        if (updatedExpense.getReceiptPath() != null) {
            expense.setReceiptPath(updatedExpense.getReceiptPath());
        }
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with ID: " + id);
        }
        expenseRepository.deleteById(id);
    }

    // ==========================================
    // QUOTATIONS CRUD
    // ==========================================

    public Quotation createQuotation(Quotation quotation) {
        Double labor = quotation.getLaborCost() != null ? quotation.getLaborCost() : 0.0;
        Double material = quotation.getMaterialCost() != null ? quotation.getMaterialCost() : 0.0;
        Double overhead = quotation.getOverheadCost() != null ? quotation.getOverheadCost() : 0.0;

        if (labor < 0.0 || material < 0.0 || overhead < 0.0) {
            throw new IllegalArgumentException("Quotation cost components (labor, material, overhead) cannot be negative.");
        }

        quotation.setLaborCost(labor);
        quotation.setMaterialCost(material);
        quotation.setOverheadCost(overhead);
        quotation.setTotalAmount(labor + material + overhead);

        if (quotation.getStatus() == null || quotation.getStatus().trim().isEmpty()) {
            quotation.setStatus("DRAFT");
        }
        return quotationRepository.save(quotation);
    }

    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll();
    }

    public Quotation getQuotationById(Long id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (quotation.isPresent()) {
            return quotation.get();
        } else {
            throw new RuntimeException("Quotation not found with ID: " + id);
        }
    }

    public List<Quotation> getQuotationsByClientEmail(String clientEmail) {
        return quotationRepository.findByClientEmail(clientEmail);
    }

    public Quotation updateQuotation(Long id, Quotation updatedQuotation) {
        Double labor = updatedQuotation.getLaborCost() != null ? updatedQuotation.getLaborCost() : 0.0;
        Double material = updatedQuotation.getMaterialCost() != null ? updatedQuotation.getMaterialCost() : 0.0;
        Double overhead = updatedQuotation.getOverheadCost() != null ? updatedQuotation.getOverheadCost() : 0.0;

        if (labor < 0.0 || material < 0.0 || overhead < 0.0) {
            throw new IllegalArgumentException("Quotation cost components (labor, material, overhead) cannot be negative.");
        }

        Quotation quotation = getQuotationById(id);
        quotation.setClientName(updatedQuotation.getClientName());
        quotation.setClientEmail(updatedQuotation.getClientEmail());
        quotation.setProjectName(updatedQuotation.getProjectName());
        quotation.setGeneratedDate(updatedQuotation.getGeneratedDate());
        quotation.setStatus(updatedQuotation.getStatus());

        quotation.setLaborCost(labor);
        quotation.setMaterialCost(material);
        quotation.setOverheadCost(overhead);
        quotation.setTotalAmount(labor + material + overhead);

        return quotationRepository.save(quotation);
    }

    public void deleteQuotation(Long id) {
        if (!quotationRepository.existsById(id)) {
            throw new RuntimeException("Quotation not found with ID: " + id);
        }
        quotationRepository.deleteById(id);
    }

    // Decorator Pattern: Dynamically calculate decorated quotation with add-ons and taxes
    public Map<String, Object> calculateCustomQuotationEstimate(
            Long quotationId,
            boolean includeArchitecturalPlan,
            boolean includeLuxuryFinishes,
            boolean includeGovTax) {

        Quotation quotation = getQuotationById(quotationId);
        double baseTotal = (quotation.getLaborCost() != null ? quotation.getLaborCost() : 0.0)
                + (quotation.getMaterialCost() != null ? quotation.getMaterialCost() : 0.0)
                + (quotation.getOverheadCost() != null ? quotation.getOverheadCost() : 0.0);

        // Concrete Component (like SimpleCoffee in lecture)
        QuotationComponent quote = new BaseQuotationComponent(quotation.getProjectName(), baseTotal);

        // Dynamically wrap with decorators based on customer selection (like MilkDecorator, SugarDecorator)
        if (includeArchitecturalPlan) {
            quote = new ArchitecturalPlanDecorator(quote);
        }
        if (includeLuxuryFinishes) {
            quote = new LuxuryFinishDecorator(quote);
        }
        if (includeGovTax) {
            quote = new GovernmentTaxVatDecorator(quote);
        }

        return Map.of(
                "quotationId", quotation.getId(),
                "projectName", quotation.getProjectName(),
                "baseCost", baseTotal,
                "decoratedDescription", quote.getDescription(),
                "finalDecoratedTotal", quote.getCost()
        );
    }
}
