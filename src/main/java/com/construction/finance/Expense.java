package com.construction.finance;

//developed by Sehansa Pahanmi (IT25103433)

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
 * Author: Pahanmi S.B.G. (IT25103433)
 * Project Construction Expense Voucher Entity
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    // Association relationship: Expense has a Project
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(name = "expense_title", nullable = false, length = 200)
    private String expenseTitle;

    @Column(nullable = false, length = 100)
    private String category; // LABOR, MATERIALS, EQUIPMENT, TRANSPORT, UTILITIES, MISC

    @Column(nullable = false)
    private Double amount = 0.0;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "receipt_path", length = 255)
    private String receiptPath;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public Expense() {}

    // Parameterized Constructor
    public Expense(Long projectId, String expenseTitle, String category, Double amount, LocalDate expenseDate) {
        this.projectId = projectId;
        this.expenseTitle = expenseTitle;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
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

    public String getExpenseTitle() { return expenseTitle; }
    public void setExpenseTitle(String expenseTitle) { this.expenseTitle = expenseTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCustomId() {
        return id != null ? String.format("EXP%03d", id) : "EXP---";
    }
}
