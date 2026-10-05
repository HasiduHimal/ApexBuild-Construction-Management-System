package com.construction.finance;

import com.construction.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author: Pahanmi S.B.G. (IT25104485)
 * Project Quotation (Cost Estimation & BOQ) Entity
 */
@Entity
@Table(name = "quotations")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    // Association relationship: Quotation has a Project
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(name = "client_name", nullable = false, length = 150)
    private String clientName;

    @Column(name = "client_email", nullable = false, length = 150)
    private String clientEmail;

    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName;

    @Column(name = "labor_cost")
    private Double laborCost = 0.0;

    @Column(name = "material_cost")
    private Double materialCost = 0.0;

    @Column(name = "overhead_cost")
    private Double overheadCost = 0.0;

    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(length = 50)
    private String status = "DRAFT"; // DRAFT, SENT, ACCEPTED, REJECTED

    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public Quotation() {}

    // Parameterized Constructor
    public Quotation(Long projectId, String clientName, String clientEmail, String projectName,
                     Double laborCost, Double materialCost, Double overheadCost, LocalDate generatedDate) {
        this.projectId = projectId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.projectName = projectName;
        this.laborCost = laborCost != null ? laborCost : 0.0;
        this.materialCost = materialCost != null ? materialCost : 0.0;
        this.overheadCost = overheadCost != null ? overheadCost : 0.0;
        this.totalAmount = this.laborCost + this.materialCost + this.overheadCost;
        this.generatedDate = generatedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Project getProject() { return project; }
    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            this.projectId = project.getId();
        }
    }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }

    public Double getMaterialCost() { return materialCost; }
    public void setMaterialCost(Double materialCost) { this.materialCost = materialCost; }

    public Double getOverheadCost() { return overheadCost; }
    public void setOverheadCost(Double overheadCost) { this.overheadCost = overheadCost; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }

    public String getCustomId() {
        return id != null ? String.format("QUO%03d", id) : "QUO---";
    }
}
