package com.construction.inventory;

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
 * Author: Bandara R.A.H.G.D. (IT25101623)
 * Material Disbursement Voucher Entity
 */
@Entity
@Table(name = "material_issues")
public class MaterialIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    // Association relationship: MaterialIssue has a Material
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", insertable = false, updatable = false)
    private Material material;

    @Column(name = "project_id")
    private Long projectId;

    // Association relationship: MaterialIssue has a Project
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(name = "issued_to", nullable = false, length = 150)
    private String issuedTo;

    @Column(name = "quantity_issued", nullable = false)
    private Integer quantityIssued;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public MaterialIssue() {}

    // Parameterized Constructor
    public MaterialIssue(Long materialId, Long projectId, String issuedTo, Integer quantityIssued, LocalDate issueDate) {
        this.materialId = materialId;
        this.projectId = projectId;
        this.issuedTo = issuedTo;
        this.quantityIssued = quantityIssued;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) {
        this.material = material;
        if (material != null) {
            this.materialId = material.getId();
        }
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Project getProject() { return project; }
    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            this.projectId = project.getId();
        }
    }

    public String getIssuedTo() { return issuedTo; }
    public void setIssuedTo(String issuedTo) { this.issuedTo = issuedTo; }

    public Integer getQuantityIssued() { return quantityIssued; }
    public void setQuantityIssued(Integer quantityIssued) { this.quantityIssued = quantityIssued; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCustomId() {
        return id != null ? String.format("ISU%03d", id) : "ISU---";
    }
}
