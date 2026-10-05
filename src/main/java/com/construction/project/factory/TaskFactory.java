package com.construction.project.factory;

/**
 * Factory class for generating TaskPlan instances.
 * Follows the Factory Method Design Pattern (SE2030 Lecture Part II).
 */
public class TaskFactory {

    // Factory method to instantiate the correct TaskPlan without exposing concrete classes
    public static TaskPlan createTaskPlan(String taskType) {
        if (taskType == null || taskType.trim().isEmpty()) {
            return new StructuralTaskPlan();
        }

        String type = taskType.toUpperCase();
        if (type.contains("ELECTRICAL") || type.contains("WIRING") || type.contains("LIGHTING")) {
            return new ElectricalTaskPlan();
        } else if (type.contains("PLUMBING") || type.contains("PIPE") || type.contains("DRAINAGE")) {
            return new PlumbingTaskPlan();
        } else if (type.contains("FINISH") || type.contains("PAINT") || type.contains("TILE") || type.contains("PLASTER")) {
            return new FinishingTaskPlan();
        } else {
            return new StructuralTaskPlan();
        }
    }
}
