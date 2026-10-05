package com.construction.procurement.strategy;

//developed by vaishnavy.s (IT25101549)


import java.util.UUID;

/**
 * Concrete Strategy: Direct Electronic Bank Wire Transfer.
 * (SE2030 Lecture Part II, Slide 18-19).
 */
public class BankTransferPaymentStrategy implements SupplierPaymentStrategy {

    private final String bankAccount;

    public BankTransferPaymentStrategy(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public String getPaymentMethodName() {
        return "BANK_TRANSFER";
    }

    @Override
    public String processPayment(Long purchaseOrderId, double amount) {
        String ref = "SL-EFT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "Paid LKR " + amount + " to Supplier Account (" + bankAccount + ") for PO #" + purchaseOrderId + " via Direct Bank Transfer. Reference: " + ref;
    }
}
