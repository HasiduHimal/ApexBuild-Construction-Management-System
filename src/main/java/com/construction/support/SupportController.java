package com.construction.support;
// Developed & Verified by Weerawansha K.H.H. (IT25103631)

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    // ==========================================
    // INQUIRIES & COMPLAINTS REST APIS (CRUD)
    // ==========================================

    @PostMapping("/inquiries")
    public ResponseEntity<Inquiry> createInquiry(@RequestBody Inquiry inquiry) {
        return ResponseEntity.ok(supportService.createInquiry(inquiry));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<Inquiry>> getAllInquiries(@RequestParam(name = "clientEmail", required = false) String clientEmail) {
        if (clientEmail != null && !clientEmail.trim().isEmpty()) {
            return ResponseEntity.ok(supportService.getInquiriesByClientEmail(clientEmail));
        }
        return ResponseEntity.ok(supportService.getAllInquiries());
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<Inquiry> getInquiryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(supportService.getInquiryById(id));
    }

    @PutMapping("/inquiries/{id}")
    public ResponseEntity<Inquiry> updateInquiry(@PathVariable("id") Long id, @RequestBody Inquiry inquiry) {
        return ResponseEntity.ok(supportService.updateInquiry(id, inquiry));
    }

    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Map<String, String>> deleteInquiry(@PathVariable("id") Long id) {
        supportService.deleteInquiry(id);
        return ResponseEntity.ok(Map.of("message", "Inquiry deleted successfully", "id", String.valueOf(id)));
    }

    // ==========================================
    // DAILY PROGRESS LOGS REST APIS (CRUD)
    // ==========================================

    @PostMapping("/progress")
    public ResponseEntity<ProgressLog> createProgressLog(@RequestBody ProgressLog log) {
        return ResponseEntity.ok(supportService.createProgressLog(log));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ProgressLog>> getAllProgressLogs(@RequestParam(name = "projectId", required = false) Long projectId) {
        if (projectId != null) {
            return ResponseEntity.ok(supportService.getProgressLogsByProject(projectId));
        }
        return ResponseEntity.ok(supportService.getAllProgressLogs());
    }

    @PutMapping("/progress/{id}")
    public ResponseEntity<ProgressLog> updateProgressLog(@PathVariable("id") Long id, @RequestBody ProgressLog log) {
        return ResponseEntity.ok(supportService.updateProgressLog(id, log));
    }

    @DeleteMapping("/progress/{id}")
    public ResponseEntity<Map<String, String>> deleteProgressLog(@PathVariable("id") Long id) {
        supportService.deleteProgressLog(id);
        return ResponseEntity.ok(Map.of("message", "Progress log deleted successfully", "id", String.valueOf(id)));
    }

    // Observer Pattern Demonstration Endpoint
    @GetMapping("/notifications")
    public ResponseEntity<List<String>> getObserverNotifications() {
        return ResponseEntity.ok(supportService.getRecentObserverNotifications());
    }
}
