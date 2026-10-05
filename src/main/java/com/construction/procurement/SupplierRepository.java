package com.construction.procurement;

//developed by vaishnavy.s(IT25101549)

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByStatus(String status);
    List<Supplier> findBySupplyCategory(String supplyCategory);
}
