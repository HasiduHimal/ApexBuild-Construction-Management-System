package com.construction.procurement.strategy;

//developed by vaishnavy.s(IT25101549)


/**
 * Context class that delegates payment processing to the currently selected Strategy.
 * Follows the Strategy Design Pattern (SE2030 Lecture Part II, Slides 15-16).
 */
public class ProcurementPaymentContext {

    private SupplierPaymentStrategy paymentStrategy;

    public ProcurementPaymentContext() {
    }

    public ProcurementPaymentContext(SupplierPaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    // Set or switch the strategy at runtime
    public void setPaymentStrategy(SupplierPaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public String executePayment(Long purchaseOrderId, double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("No payment strategy selected for this purchase order!");
        }
        return paymentStrategy.processPayment(purchaseOrderId, amount);
    }
}
