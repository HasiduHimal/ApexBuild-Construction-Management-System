package com.construction.project.factory;

public class ElectricalTaskPlan implements TaskPlan {

    @Override
    public String getCategory() {
        return "ELECTRICAL";
    }

    @Override
    public int getDefaultDurationDays() {
        return 7;
    }

    @Override
    public String getSafetyRequirements() {
        return "Insulated safety gloves, safety goggles, and lockout/tagout procedure required.";
    }
}
