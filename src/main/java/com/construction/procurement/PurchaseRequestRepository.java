package com.construction.procurement;
//developed by vaishnavy.s (IT25101549)


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByStatus(String status);
    List<PurchaseRequest> findBySupplierId(Long supplierId);
}
