package com.construction.project.factory;

public class StructuralTaskPlan implements TaskPlan {

    @Override
    public String getCategory() {
        return "STRUCTURAL";
    }

    @Override
    public int getDefaultDurationDays() {
        return 14;
    }

    @Override
    public String getSafetyRequirements() {
        return "Hard hat, steel-toe safety boots, and high-visibility vest required.";
    }
}
