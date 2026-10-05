package com.construction.inventory;

// Developed & Verified by Bandara R.A.H.G.D (IT25101722)

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialIssueRepository extends JpaRepository<MaterialIssue, Long> {
    List<MaterialIssue> findByProjectId(Long projectId);
    List<MaterialIssue> findByMaterialId(Long materialId);
}
