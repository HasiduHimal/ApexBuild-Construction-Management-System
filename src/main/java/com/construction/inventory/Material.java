package com.construction.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Author: Bandara R.A.H.G.D. (IT25101623)
 * Construction Material Item Entity
 */
@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_name", nullable = false, length = 200)
    private String materialName;

    @Column(nullable = false, length = 100)
    private String category; // CEMENT, STEEL, SAND, BRICKS, ELECTRICAL, PLUMBING, OTHER

    @Column(nullable = false, length = 50)
    private String unit; // Bags, Tons, Cubes, Units, Meters

    @Column(name = "unit_price")
    private Double unitPrice = 0.0;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 10;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public Material() {}

    // Parameterized Constructor
    public Material(String materialName, String category, String unit, Double unitPrice, Integer currentStock) {
        this.materialName = materialName;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.currentStock = currentStock;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomId() {
        return id != null ? String.format("MID%03d", id) : "MID---";
    }
}
