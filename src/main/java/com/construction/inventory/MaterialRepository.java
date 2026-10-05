package com.construction.inventory;

// Developed & Verified by Bandara R.A.H.G.D (IT25101722)


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByCategory(String category);
    List<Material> findByStatus(String status);
    Optional<Material> findByMaterialNameIgnoreCase(String materialName);
}
