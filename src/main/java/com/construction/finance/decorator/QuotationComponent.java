package com.construction.finance.decorator;
//developed by Sehansa Pahanmi (IT25103433)
/**
 * Component interface for Quotation.
 * Follows the Decorator Design Pattern (SE2030 Lecture Part II, Slide 43).
 */
public interface QuotationComponent {
    String getDescription();
    double getCost();
}
