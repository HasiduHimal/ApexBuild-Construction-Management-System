package com.construction.procurement.strategy;

import java.util.UUID;

/**
 * Concrete Strategy: Commercial Letter of Credit (LC) / 30-Day Trade Credit.
 * (SE2030 Lecture Part II, Slide 18-19).
 */
public class LetterOfCreditPaymentStrategy implements SupplierPaymentStrategy {

    private final String issuingBank;

    public LetterOfCreditPaymentStrategy(String issuingBank) {
        this.issuingBank = issuingBank;
    }

    @Override
    public String getPaymentMethodName() {
        return "LETTER_OF_CREDIT";
    }

    @Override
    public String processPayment(Long purchaseOrderId, double amount) {
        String lcNumber = "LC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "Issued Commercial 30-Day LC (" + lcNumber + ") through " + issuingBank + " for PO #" + purchaseOrderId + " (Total: LKR " + amount + ")";
    }
}
