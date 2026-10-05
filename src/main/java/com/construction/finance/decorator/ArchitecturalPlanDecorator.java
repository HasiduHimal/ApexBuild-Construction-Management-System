package com.construction.finance.decorator;
//developed by Sehansa Pahanmi (IT25103433)

/**
 * Concrete Decorator: 3D Architectural Blueprint & Engineering Review (+150,000 LKR).
 * (SE2030 Lecture Part II, Slide 45).
 */
public class ArchitecturalPlanDecorator extends QuotationDecorator {

    private final double architecturalFee = 150000.0;

    public ArchitecturalPlanDecorator(QuotationComponent quotation) {
        super(quotation);
    }

    @Override
    public String getDescription() {
        return decoratedQuotation.getDescription() + " + 3D Architectural Blueprint";
    }

    @Override
    public double getCost() {
        return decoratedQuotation.getCost() + architecturalFee;
    }
}
