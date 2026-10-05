package com.construction.project;
// Developed & Verified by Wijesekera S.D.R. (IT25102552).

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(String status);
    List<Project> findByClientId(Long clientId);
    Optional<Project> findByProjectName(String projectName);
}
