package com.construction.finance.decorator;
//developed by Sehansa Pahanmi (IT25103433)

/**
 * Concrete Component representing the base construction quotation.
 * (SE2030 Lecture Part II, Slide 43).
 */
public class BaseQuotationComponent implements QuotationComponent {

    private final String projectName;
    private final double baseCost;

    public BaseQuotationComponent(String projectName, double baseCost) {
        this.projectName = projectName;
        this.baseCost = baseCost;
    }

    @Override
    public String getDescription() {
        return "Base Construction Package [" + projectName + "]";
    }

    @Override
    public double getCost() {
        return baseCost;
    }
}
