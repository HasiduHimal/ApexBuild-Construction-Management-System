package com.construction.finance;
//developed by Sehansa Pahanmi (IT25103433)

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByClientEmail(String clientEmail);
    List<Quotation> findByProjectId(Long projectId);
    List<Quotation> findByStatus(String status);
}
