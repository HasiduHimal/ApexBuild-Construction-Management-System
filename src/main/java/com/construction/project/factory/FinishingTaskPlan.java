package com.construction.project.factory;

public class FinishingTaskPlan implements TaskPlan {

    @Override
    public String getCategory() {
        return "FINISHING";
    }

    @Override
    public int getDefaultDurationDays() {
        return 10;
    }

    @Override
    public String getSafetyRequirements() {
        return "Respirator masks, eye protection glasses, and surface dust extraction required.";
    }
}
