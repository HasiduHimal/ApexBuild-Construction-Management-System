package com.construction.support;
// Developed & Verified by Weerawansha K.H.H. (IT25103631).

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Author: Weerawansha K.H.H. (IT25103631)
 * Client Support Inquiries & Tickets Entity
 */
@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(nullable = false, length = 150)
    private String clientName;

    @Column(nullable = false, length = 150)
    private String clientEmail;

    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(columnDefinition = "VARCHAR(MAX)", nullable = false)
    private String message;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String resolution;

    @Column(length = 50)
    private String status = "OPEN"; // OPEN, INVESTIGATING, RESOLVED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Inquiry() {}

    public Inquiry(String clientName, String clientEmail, String subject, String message) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.subject = subject;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
