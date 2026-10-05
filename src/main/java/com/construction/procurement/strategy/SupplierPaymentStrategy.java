package com.construction.procurement.strategy;

//developed by vaishnavy.s (IT25101549)


/**
 * Strategy interface for supplier purchase order payments.
 * Follows the Strategy Design Pattern (SE2030 Lecture Part II, Slide 17).
 */
public interface SupplierPaymentStrategy {
    String getPaymentMethodName();
    String processPayment(Long purchaseOrderId, double amount);
}
