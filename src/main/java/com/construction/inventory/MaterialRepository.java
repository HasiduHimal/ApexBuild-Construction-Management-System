package com.construction.inventory;

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
