package com.construction.procurement;
//developed by vaishnavy.s(IT25101549)


import com.construction.procurement.strategy.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProcurementService {

    private final SupplierRepository supplierRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

    public ProcurementService(SupplierRepository supplierRepository, PurchaseRequestRepository purchaseRequestRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
    }

    // ==========================================
    // SUPPLIERS CRUD
    // ==========================================

    private void validatePhoneNumber(String phone) {
        if (phone == null || !phone.trim().replaceAll("[\\s-]", "").matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Contact number must contain exactly 10 digits (e.g., 0712345678).");
        }
    }

    public Supplier createSupplier(Supplier supplier) {
        validatePhoneNumber(supplier.getPhone());
        supplier.setPhone(supplier.getPhone().trim().replaceAll("[\\s-]", ""));
        if (supplier.getStatus() == null || supplier.getStatus().trim().isEmpty()) {
            supplier.setStatus("ACTIVE");
        }
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        Optional<Supplier> opt = supplierRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new RuntimeException("Supplier not found with ID: " + id);
        }
    }

    public List<Supplier> getSuppliersByCategory(String category) {
        return supplierRepository.findBySupplyCategory(category);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        validatePhoneNumber(updatedSupplier.getPhone());
        Supplier supplier = getSupplierById(id);
        supplier.setCompanyName(updatedSupplier.getCompanyName());
        supplier.setContactPerson(updatedSupplier.getContactPerson());
        supplier.setPhone(updatedSupplier.getPhone().trim().replaceAll("[\\s-]", ""));
        supplier.setEmail(updatedSupplier.getEmail());
        supplier.setAddress(updatedSupplier.getAddress());
        supplier.setSupplyCategory(updatedSupplier.getSupplyCategory());
        supplier.setStatus(updatedSupplier.getStatus());
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    // ==========================================
    // PURCHASE REQUESTS CRUD
    // ==========================================

    public PurchaseRequest createPurchaseRequest(PurchaseRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new IllegalArgumentException("Order quantity must be at least 1.");
        }
        if (request.getEstimatedCost() == null || request.getEstimatedCost() <= 0.0) {
            throw new IllegalArgumentException("Estimated cost must be greater than zero.");
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus("PENDING");
        }
        return purchaseRequestRepository.save(request);
    }

    public List<PurchaseRequest> getAllPurchaseRequests() {
        return purchaseRequestRepository.findAll();
    }

    public PurchaseRequest getPurchaseRequestById(Long id) {
        Optional<PurchaseRequest> opt = purchaseRequestRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new RuntimeException("Purchase request not found with ID: " + id);
        }
    }

    public List<PurchaseRequest> getPurchaseRequestsByStatus(String status) {
        return purchaseRequestRepository.findByStatus(status);
    }

    public PurchaseRequest updatePurchaseRequest(Long id, PurchaseRequest updatedRequest) {
        if (updatedRequest.getQuantity() == null || updatedRequest.getQuantity() < 1) {
            throw new IllegalArgumentException("Order quantity must be at least 1.");
        }
        if (updatedRequest.getEstimatedCost() == null || updatedRequest.getEstimatedCost() <= 0.0) {
            throw new IllegalArgumentException("Estimated cost must be greater than zero.");
        }

        PurchaseRequest request = getPurchaseRequestById(id);
        request.setSupplierId(updatedRequest.getSupplierId());
        request.setMaterialName(updatedRequest.getMaterialName());
        request.setQuantity(updatedRequest.getQuantity());
        request.setEstimatedCost(updatedRequest.getEstimatedCost());
        request.setStatus(updatedRequest.getStatus());
        request.setRequestDate(updatedRequest.getRequestDate());
        request.setNotes(updatedRequest.getNotes());
        return purchaseRequestRepository.save(request);
    }

    public void deletePurchaseRequest(Long id) {
        if (!purchaseRequestRepository.existsById(id)) {
            throw new RuntimeException("Purchase request not found with ID: " + id);
        }
        purchaseRequestRepository.deleteById(id);
    }

    // Strategy Pattern: Settle supplier payment using selected strategy at runtime
    public Map<String, Object> processPurchaseOrderPayment(Long id, String paymentMethod) {
        PurchaseRequest po = getPurchaseRequestById(id);
        double amount = po.getEstimatedCost() != null ? po.getEstimatedCost() : 0.0;

        SupplierPaymentStrategy strategy;
        if ("CREDIT_CARD".equalsIgnoreCase(paymentMethod)) {
            strategy = new CorporateCreditCardPaymentStrategy("XXXX-XXXX-XXXX-4819");
        } else if ("LETTER_OF_CREDIT".equalsIgnoreCase(paymentMethod)) {
            strategy = new LetterOfCreditPaymentStrategy("Bank of Ceylon (BOC)");
        } else {
            strategy = new BankTransferPaymentStrategy("Commercial Bank AC# 8001293847");
        }

        // Context delegates execution to the selected strategy
        ProcurementPaymentContext context = new ProcurementPaymentContext();
        context.setPaymentStrategy(strategy);
        String receipt = context.executePayment(po.getId(), amount);

        po.setStatus("PAID");
        po.setNotes((po.getNotes() != null ? po.getNotes() + " | " : "") + receipt);
        purchaseRequestRepository.save(po);

        return Map.of(
                "purchaseOrderId", po.getId(),
                "status", po.getStatus(),
                "paymentMethod", strategy.getPaymentMethodName(),
                "amount", amount,
                "receipt", receipt
        );
    }
}
