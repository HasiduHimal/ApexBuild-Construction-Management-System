package com.construction.finance.decorator;
//developed by Sehansa Pahanmi (IT25103433)

/**
 * Concrete Decorator: Luxury Finishes, Premium Porcelain & Teak Fittings (+250,000 LKR).
 * (SE2030 Lecture Part II, Slide 45).
 */
public class LuxuryFinishDecorator extends QuotationDecorator {

    private final double luxuryFinishFee = 250000.0;

    public LuxuryFinishDecorator(QuotationComponent quotation) {
        super(quotation);
    }

    @Override
    public String getDescription() {
        return decoratedQuotation.getDescription() + " + Luxury Interior Finishes";
    }

    @Override
    public double getCost() {
        return decoratedQuotation.getCost() + luxuryFinishFee;
    }
}
