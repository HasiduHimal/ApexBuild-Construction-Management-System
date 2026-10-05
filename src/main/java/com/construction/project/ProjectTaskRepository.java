package com.construction.project;
// Developed & Verified by Wijesekera S.D.R. (IT25102552).

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectId(Long projectId);
    List<ProjectTask> findByStatus(String status);
    List<ProjectTask> findByAssignedWorker(String assignedWorker);
}
