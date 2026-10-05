package com.construction.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByClientId(Long clientId);
    List<Inquiry> findByStatus(String status);
    List<Inquiry> findByClientEmail(String clientEmail);
}
