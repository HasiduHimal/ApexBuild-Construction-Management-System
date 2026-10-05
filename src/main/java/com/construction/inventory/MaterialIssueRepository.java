package com.construction.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialIssueRepository extends JpaRepository<MaterialIssue, Long> {
    List<MaterialIssue> findByProjectId(Long projectId);
    List<MaterialIssue> findByMaterialId(Long materialId);
}
