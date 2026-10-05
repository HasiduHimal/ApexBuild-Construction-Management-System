package com.construction.project;
// Developed & Verified by Wijesekera S.D.R. (IT25102552)

import com.construction.project.factory.TaskFactory;
import com.construction.project.factory.TaskPlan;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectTaskRepository projectTaskRepository) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    // ==========================================
    // PROJECTS CRUD
    // ==========================================

    public Project createProject(Project project) {
        if (project.getProjectName() == null || project.getProjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required.");
        }

        // Validate Unique Project Name
        if (projectRepository.findByProjectName(project.getProjectName().trim()).isPresent()) {
            throw new IllegalArgumentException("A project with the name '" + project.getProjectName().trim() + "' already exists. Please choose a unique name.");
        }

        // Validate Budget: must be >= 0
        if (project.getEstimatedBudget() != null && project.getEstimatedBudget() < 0) {
            throw new IllegalArgumentException("Estimated budget must be greater than or equal to zero.");
        }

        // Validate Dates: end date must be after or equal to start date
        if (project.getStartDate() != null && project.getEndDate() != null && project.getEndDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("Project end date cannot be earlier than start date.");
        }

        if (project.getStatus() == null || project.getStatus().trim().isEmpty()) {
            project.setStatus("PLANNED");
        }
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            return project.get();
        } else {
            throw new RuntimeException("Project not found with ID: " + id);
        }
    }

    public List<Project> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    public Project updateProject(Long id, Project updatedProject) {
        Project project = getProjectById(id);

        if (updatedProject.getProjectName() == null || updatedProject.getProjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required.");
        }

        // Validate Unique Project Name (check if changed to an existing name of another project)
        Optional<Project> existing = projectRepository.findByProjectName(updatedProject.getProjectName().trim());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("A project with the name '" + updatedProject.getProjectName().trim() + "' already exists. Please choose a unique name.");
        }

        // Validate Budget: must be >= 0
        if (updatedProject.getEstimatedBudget() != null && updatedProject.getEstimatedBudget() < 0) {
            throw new IllegalArgumentException("Estimated budget must be greater than or equal to zero.");
        }

        // Validate Dates: end date must be after or equal to start date
        if (updatedProject.getStartDate() != null && updatedProject.getEndDate() != null && updatedProject.getEndDate().isBefore(updatedProject.getStartDate())) {
            throw new IllegalArgumentException("Project end date cannot be earlier than start date.");
        }

        project.setProjectName(updatedProject.getProjectName().trim());
        project.setDescription(updatedProject.getDescription());
        project.setLocation(updatedProject.getLocation());
        project.setClientName(updatedProject.getClientName());
        project.setStartDate(updatedProject.getStartDate());
        project.setEndDate(updatedProject.getEndDate());
        project.setEstimatedBudget(updatedProject.getEstimatedBudget());
        project.setStatus(updatedProject.getStatus());
        if (updatedProject.getHousePlanFile() != null) {
            project.setHousePlanFile(updatedProject.getHousePlanFile());
        }
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
    }

    // ==========================================
    // PROJECT TASKS CRUD
    // ==========================================

    public ProjectTask createTask(ProjectTask task) {
        if (!projectRepository.existsById(task.getProjectId())) {
            throw new RuntimeException("Cannot create task. Project with ID " + task.getProjectId() + " does not exist!");
        }
        if (task.getStatus() == null || task.getStatus().trim().isEmpty()) {
            task.setStatus("TODO");
        }

        // Apply Factory Pattern: resolve task specifications & safety rules
        TaskPlan plan = TaskFactory.createTaskPlan(task.getTaskName());
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            task.setDescription("[" + plan.getCategory() + "] Safety protocol: " + plan.getSafetyRequirements());
        }

        return projectTaskRepository.save(task);
    }

    public TaskPlan getTaskPlan(String taskType) {
        // Factory Method pattern lookup
        return TaskFactory.createTaskPlan(taskType);
    }

    private int getPriorityWeight(String priority) {
        if (priority == null) return 2;
        switch (priority.trim().toUpperCase()) {
            case "URGENT": return 0;
            case "HIGH": return 1;
            case "MEDIUM": return 2;
            case "LOW": return 3;
            default: return 2;
        }
    }

    public List<ProjectTask> getAllTasks() {
        List<ProjectTask> tasks = projectTaskRepository.findAll();
        tasks.sort(java.util.Comparator.comparingInt(t -> getPriorityWeight(t.getPriority())));
        return tasks;
    }

    public List<ProjectTask> getTasksByProject(Long projectId) {
        List<ProjectTask> tasks = projectTaskRepository.findByProjectId(projectId);
        tasks.sort(java.util.Comparator.comparingInt(t -> getPriorityWeight(t.getPriority())));
        return tasks;
    }

    public ProjectTask getTaskById(Long id) {
        Optional<ProjectTask> task = projectTaskRepository.findById(id);
        if (task.isPresent()) {
            return task.get();
        } else {
            throw new RuntimeException("Task not found with ID: " + id);
        }
    }

    public ProjectTask updateTask(Long id, ProjectTask updatedTask) {
        ProjectTask task = getTaskById(id);
        task.setTaskName(updatedTask.getTaskName());
        task.setDescription(updatedTask.getDescription());
        task.setAssignedWorker(updatedTask.getAssignedWorker());
        task.setPriority(updatedTask.getPriority());
        task.setStatus(updatedTask.getStatus());
        task.setDueDate(updatedTask.getDueDate());
        return projectTaskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!projectTaskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        projectTaskRepository.deleteById(id);
    }
}
