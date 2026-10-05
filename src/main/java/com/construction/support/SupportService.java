package com.construction.support;

import com.construction.support.observer.ClientNotificationObserver;
import com.construction.support.observer.QualityProgressSubject;
import com.construction.support.observer.SiteEngineerObserver;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SupportService {

    private final InquiryRepository inquiryRepository;
    private final ProgressLogRepository progressLogRepository;

    // Observer Pattern: Subject instance managing progress and ticket alerts
    private final QualityProgressSubject progressSubject = new QualityProgressSubject();

    public SupportService(InquiryRepository inquiryRepository, ProgressLogRepository progressLogRepository) {
        this.inquiryRepository = inquiryRepository;
        this.progressLogRepository = progressLogRepository;

        // Register default concrete observers
        progressSubject.addObserver(new ClientNotificationObserver("Client-Portal-Feed"));
        progressSubject.addObserver(new SiteEngineerObserver("Eng-Weerawansha-HQ"));
    }

    // ==========================================
    // INQUIRIES & COMPLAINTS CRUD
    // ==========================================

    public Inquiry createInquiry(Inquiry inquiry) {
        if (inquiry.getStatus() == null) {
            inquiry.setStatus("OPEN");
        }
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public Inquiry getInquiryById(Long id) {
        Optional<Inquiry> inquiry = inquiryRepository.findById(id);
        if (inquiry.isPresent()) {
            return inquiry.get();
        } else {
            throw new RuntimeException("Inquiry not found with ID: " + id);
        }
    }

    public List<Inquiry> getInquiriesByClientEmail(String email) {
        return inquiryRepository.findByClientEmail(email);
    }

    public Inquiry updateInquiry(Long id, Inquiry updatedInquiry) {
        Inquiry inquiry = getInquiryById(id);

        if ("RESOLVED".equalsIgnoreCase(updatedInquiry.getStatus()) && 
                (updatedInquiry.getResolution() == null || updatedInquiry.getResolution().trim().isEmpty())) {
            throw new IllegalArgumentException("Resolution details are required when marking an inquiry as RESOLVED.");
        }

        inquiry.setSubject(updatedInquiry.getSubject());
        inquiry.setMessage(updatedInquiry.getMessage());
        inquiry.setStatus(updatedInquiry.getStatus());
        inquiry.setResolution(updatedInquiry.getResolution());
        Inquiry saved = inquiryRepository.save(inquiry);

        // Notify observers if inquiry is resolved
        if ("RESOLVED".equalsIgnoreCase(saved.getStatus())) {
            progressSubject.notifyObservers("Inquiry #" + saved.getId() + " marked RESOLVED for " + saved.getClientName() + ": " + saved.getResolution());
        }

        return saved;
    }

    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new RuntimeException("Inquiry not found with ID: " + id);
        }
        inquiryRepository.deleteById(id);
    }

    // ==========================================
    // DAILY SITE PROGRESS LOGS CRUD
    // ==========================================

    public ProgressLog createProgressLog(ProgressLog log) {
        if (log.getMilestone() == null || log.getMilestone().trim().isEmpty()) {
            throw new IllegalArgumentException("Milestone title cannot be empty.");
        }
        if (log.getWorkDone() == null || log.getWorkDone().trim().isEmpty()) {
            throw new IllegalArgumentException("Work performed details cannot be empty.");
        }
        if (log.getLogDate() != null && log.getLogDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Milestone log date cannot be in the future.");
        }

        ProgressLog saved = progressLogRepository.save(log);
        // Observer Pattern: Broadcast milestone update to observers
        progressSubject.notifyObservers("New Milestone Recorded for Project #" + saved.getProjectId() + 
                ": " + saved.getMilestone() + " (Reported by " + saved.getReportedBy() + ")");
        return saved;
    }

    public List<String> getRecentObserverNotifications() {
        return progressSubject.getNotificationHistory();
    }

    public List<ProgressLog> getAllProgressLogs() {
        return progressLogRepository.findAll();
    }

    public List<ProgressLog> getProgressLogsByProject(Long projectId) {
        return progressLogRepository.findByProjectId(projectId);
    }

    public ProgressLog updateProgressLog(Long id, ProgressLog updatedLog) {
        Optional<ProgressLog> opt = progressLogRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RuntimeException("Progress log not found with ID: " + id);
        }
        if (updatedLog.getMilestone() == null || updatedLog.getMilestone().trim().isEmpty()) {
            throw new IllegalArgumentException("Milestone title cannot be empty.");
        }
        if (updatedLog.getWorkDone() == null || updatedLog.getWorkDone().trim().isEmpty()) {
            throw new IllegalArgumentException("Work performed details cannot be empty.");
        }
        if (updatedLog.getLogDate() != null && updatedLog.getLogDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Milestone log date cannot be in the future.");
        }

        ProgressLog log = opt.get();
        log.setMilestone(updatedLog.getMilestone().trim());
        log.setWorkDone(updatedLog.getWorkDone().trim());
        log.setIssuesFaced(updatedLog.getIssuesFaced());
        log.setLogDate(updatedLog.getLogDate());
        log.setReportedBy(updatedLog.getReportedBy());
        return progressLogRepository.save(log);
    }

    public void deleteProgressLog(Long id) {
        if (!progressLogRepository.existsById(id)) {
            throw new RuntimeException("Progress log not found with ID: " + id);
        }
        progressLogRepository.deleteById(id);
    }
}
