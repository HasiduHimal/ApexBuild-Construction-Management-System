package com.construction.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author: Wijesekera S.D.R. (IT25102552)
 * Construction Project Entity
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String description;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name", length = 150)
    private String clientName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "estimated_budget")
    private Double estimatedBudget = 0.0;

    @Column(length = 50)
    private String status = "PLANNED"; // PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED

    @Column(name = "house_plan_file")
    private String housePlanFile;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Project() {}

    public Project(String projectName, String location, Double estimatedBudget) {
        this.projectName = projectName;
        this.location = location;
        this.estimatedBudget = estimatedBudget;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getEstimatedBudget() { return estimatedBudget; }
    public void setEstimatedBudget(Double estimatedBudget) { this.estimatedBudget = estimatedBudget; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHousePlanFile() { return housePlanFile; }
    public void setHousePlanFile(String housePlanFile) { this.housePlanFile = housePlanFile; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCustomId() {
        return id != null ? String.format("PID%03d", id) : "PID---";
    }
}
