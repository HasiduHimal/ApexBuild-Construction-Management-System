package com.construction.procurement;
//developed by vaishnavy.s(IT25101549)


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Author: Vaishnavy S. (IT25100701)
 * Construction Material Supplier Entity
 */
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "contact_person", nullable = false, length = 150)
    private String contactPerson;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(name = "supply_category", nullable = false, length = 150)
    private String supplyCategory; // CEMENT, STEEL, SAND, BRICKS, HARDWARE, etc.

    @Column(length = 50)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, BLACKLISTED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default Constructor
    public Supplier() {}

    // Parameterized Constructor
    public Supplier(String companyName, String contactPerson, String phone, String email, String supplyCategory) {
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.supplyCategory = supplyCategory;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSupplyCategory() { return supplyCategory; }
    public void setSupplyCategory(String supplyCategory) { this.supplyCategory = supplyCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomId() {
        return id != null ? String.format("SID%03d", id) : "SID---";
    }
}
