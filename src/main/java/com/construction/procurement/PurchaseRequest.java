package com.construction.procurement;
//developed by vaishnavy.s(IT25101549)


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author: Vaishnavy S. (IT25100701)
 * Material Purchase Requisition / Order Entity
 */
@Entity
@Table(name = "purchase_requests")
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id")
    private Long supplierId;

    // Association relationship: PurchaseRequest has a Supplier
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;

    @Column(name = "material_name", nullable = false, length = 200)
    private String materialName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "estimated_cost")
    private Double estimatedCost = 0.0;

    @Column(length = 50)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, DELIVERED

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public PurchaseRequest() {}

    // Parameterized Constructor
    public PurchaseRequest(Long supplierId, String materialName, Integer quantity, Double estimatedCost, LocalDate requestDate) {
        this.supplierId = supplierId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.estimatedCost = estimatedCost;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        if (supplier != null) {
            this.supplierId = supplier.getId();
        }
    }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCustomId() {
        return id != null ? String.format("PO%03d", id) : "PO---";
    }
}
