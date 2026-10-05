package com.construction.project.factory;
// Developed & Verified by Wijesekera S.D.R. (IT25102552)

public class PlumbingTaskPlan implements TaskPlan {

    @Override
    public String getCategory() {
        return "PLUMBING";
    }

    @Override
    public int getDefaultDurationDays() {
        return 5;
    }

    @Override
    public String getSafetyRequirements() {
        return "Waterproof gloves, non-slip boots, and pipe pressure release protocol required.";
    }
}
