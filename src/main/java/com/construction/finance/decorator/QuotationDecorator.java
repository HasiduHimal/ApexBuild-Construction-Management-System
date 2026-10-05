package com.construction.finance.decorator;

/**
 * Abstract Decorator class that maintains a reference to a QuotationComponent.
 * (SE2030 Lecture Part II, Slide 44).
 */
public abstract class QuotationDecorator implements QuotationComponent {

    protected QuotationComponent decoratedQuotation;

    public QuotationDecorator(QuotationComponent quotation) {
        this.decoratedQuotation = quotation;
    }

    @Override
    public String getDescription() {
        return decoratedQuotation.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedQuotation.getCost();
    }
}
