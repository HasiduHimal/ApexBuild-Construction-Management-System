package com.construction.support;

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
 * Author: Weerawansha K.H.H. (IT25103631)
 * Daily Site Progress & Certified Quality Inspection Log Entity
 */
@Entity
@Table(name = "daily_progress_logs")
public class ProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    // Association relationship: ProgressLog has a Project
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @Column(nullable = false, length = 150)
    private String reportedBy;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(nullable = false, length = 200)
    private String milestone;

    @Column(columnDefinition = "VARCHAR(MAX)", nullable = false)
    private String workDone;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String issuesFaced;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProgressLog() {}

    public ProgressLog(Long projectId, String reportedBy, LocalDate logDate, String milestone, String workDone) {
        this.projectId = projectId;
        this.reportedBy = reportedBy;
        this.logDate = logDate;
        this.milestone = milestone;
        this.workDone = workDone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Project getProject() { return project; }
    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            this.projectId = project.getId();
        }
    }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public String getMilestone() { return milestone; }
    public void setMilestone(String milestone) { this.milestone = milestone; }

    public String getWorkDone() { return workDone; }
    public void setWorkDone(String workDone) { this.workDone = workDone; }

    public String getIssuesFaced() { return issuesFaced; }
    public void setIssuesFaced(String issuesFaced) { this.issuesFaced = issuesFaced; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
