package com.construction.project.factory;
// Developed & Verified by Wijesekera S.D.R. (IT25102552).

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
