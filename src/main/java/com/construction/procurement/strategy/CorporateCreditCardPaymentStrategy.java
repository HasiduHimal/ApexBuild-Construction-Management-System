package com.construction.procurement.strategy;

import java.util.UUID;

/**
 * Concrete Strategy: Corporate Procurement Commercial Card.
 * (SE2030 Lecture Part II, Slide 18-19).
 */
public class CorporateCreditCardPaymentStrategy implements SupplierPaymentStrategy {

    private final String maskedCardNumber;

    public CorporateCreditCardPaymentStrategy(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    @Override
    public String getPaymentMethodName() {
        return "CREDIT_CARD";
    }

    @Override
    public String processPayment(Long purchaseOrderId, double amount) {
        String authCode = "AUTH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "Charged LKR " + amount + " to Corporate Procurement Card (" + maskedCardNumber + ") for PO #" + purchaseOrderId + ". Auth Code: " + authCode;
    }
}
