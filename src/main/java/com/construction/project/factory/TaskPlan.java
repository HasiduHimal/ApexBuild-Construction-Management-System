package com.construction.project.factory;

/**
 * Common TaskPlan interface for the Factory Pattern.
 * Defines standard contract for all task types.
 */
public interface TaskPlan {
    String getCategory();
    int getDefaultDurationDays();
    String getSafetyRequirements();
}
