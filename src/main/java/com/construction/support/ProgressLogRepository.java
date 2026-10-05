package com.construction.support;
// Developed & Verified by Weerawansha K.H.H. (IT25103631)

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByProjectId(Long projectId);
}
