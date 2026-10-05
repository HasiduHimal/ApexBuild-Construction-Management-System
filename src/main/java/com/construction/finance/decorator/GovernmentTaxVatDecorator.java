package com.construction.finance.decorator;

/**
 * Concrete Decorator: 18% Government VAT / SSCL Tax calculation.
 * (SE2030 Lecture Part II, Slide 45).
 */
public class GovernmentTaxVatDecorator extends QuotationDecorator {

    private final double taxRate = 0.18; // 18% standard VAT

    public GovernmentTaxVatDecorator(QuotationComponent quotation) {
        super(quotation);
    }

    @Override
    public String getDescription() {
        return decoratedQuotation.getDescription() + " + 18% Govt VAT";
    }

    @Override
    public double getCost() {
        double subtotal = decoratedQuotation.getCost();
        double totalWithTax = subtotal * (1.0 + taxRate);
        return Math.round(totalWithTax * 100.0) / 100.0;
    }
}
