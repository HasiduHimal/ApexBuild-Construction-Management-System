package com.construction.project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import com.construction.project.factory.TaskPlan;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ==========================================
    // PROJECTS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(@RequestParam(name = "clientId", required = false) Long clientId) {
        if (clientId != null) {
            return ResponseEntity.ok(projectService.getProjectsByClient(clientId));
        }
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") Long id, @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(Map.of("message", "Project deleted successfully", "id", String.valueOf(id)));
    }

    @PostMapping("/upload-plan")
    public ResponseEntity<Map<String, String>> uploadHousePlan(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file cannot be empty!");
        }

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + (originalFilename != null ? originalFilename : "plan.pdf");
        Path destination = Paths.get(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), destination);

        return ResponseEntity.ok(Map.of(
                "message", "File uploaded successfully",
                "filePath", "/uploads/" + uniqueFilename,
                "fileName", originalFilename != null ? originalFilename : uniqueFilename
        ));
    }

    // ==========================================
    // TASKS REST ENDPOINTS (CRUD)
    // ==========================================

    @PostMapping("/tasks")
    public ResponseEntity<ProjectTask> createTask(@RequestBody ProjectTask task) {
        return ResponseEntity.ok(projectService.createTask(task));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<ProjectTask>> getAllTasks(@RequestParam(name = "projectId", required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(projectService.getTasksByProject(projectId));
        }
        return ResponseEntity.ok(projectService.getAllTasks());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<ProjectTask> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getTaskById(id));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ProjectTask> updateTask(@PathVariable("id") Long id, @RequestBody ProjectTask task) {
        return ResponseEntity.ok(projectService.updateTask(id, task));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable("id") Long id) {
        projectService.deleteTask(id);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully", "id", String.valueOf(id)));
    }

    // Factory Pattern Demonstration Endpoint
    @GetMapping("/tasks/plan/{taskType}")
    public ResponseEntity<TaskPlan> getTaskPlanTemplate(@PathVariable("taskType") String taskType) {
        return ResponseEntity.ok(projectService.getTaskPlan(taskType));
    }
}
