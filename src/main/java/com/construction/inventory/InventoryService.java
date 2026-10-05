package com.construction.inventory;

import com.construction.inventory.singleton.WarehouseSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final MaterialRepository materialRepository;
    private final MaterialIssueRepository materialIssueRepository;

    public InventoryService(MaterialRepository materialRepository, MaterialIssueRepository materialIssueRepository) {
        this.materialRepository = materialRepository;
        this.materialIssueRepository = materialIssueRepository;
    }

    // ==========================================
    // MATERIALS CATALOG CRUD
    // ==========================================

    public Material createMaterial(Material material) {
        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            throw new IllegalArgumentException("Material name is required.");
        }

        // Validate Unique Material Name
        if (materialRepository.findByMaterialNameIgnoreCase(material.getMaterialName().trim()).isPresent()) {
            throw new IllegalArgumentException("Material with name '" + material.getMaterialName().trim() + "' already exists in inventory. Please update the existing stock.");
        }

        // Validate boundary: Unit Cost >= 1, Current In-Stock >= 1, Reorder Threshold >= 1
        if (material.getUnitPrice() == null || material.getUnitPrice() < 1.0) {
            throw new IllegalArgumentException("Unit Cost must be greater than or equal to 1.");
        }
        if (material.getCurrentStock() == null || material.getCurrentStock() < 1) {
            throw new IllegalArgumentException("Initial in-stock quantity must be greater than or equal to 1.");
        }
        if (material.getReorderLevel() == null || material.getReorderLevel() < 1) {
            throw new IllegalArgumentException("Reorder Threshold must be greater than or equal to 1.");
        }

        material.setMaterialName(material.getMaterialName().trim());
        if (material.getStatus() == null || material.getStatus().trim().isEmpty()) {
            material.setStatus("ACTIVE");
        }

        Material saved = materialRepository.save(material);
        // Singleton pattern call to audit catalog modification
        WarehouseSessionManager.getInstance().logActivity("Added new material to catalog: " + saved.getMaterialName() + " (" + saved.getCustomId() + ")");
        return saved;
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<Material> getActiveMaterials() {
        return materialRepository.findByStatus("ACTIVE");
    }

    public Material getMaterialById(Long id) {
        Optional<Material> opt = materialRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new RuntimeException("Material not found with ID: " + id);
        }
    }

    public List<Material> getMaterialsByCategory(String category) {
        return materialRepository.findByCategory(category);
    }

    public Material updateMaterial(Long id, Material updatedMaterial) {
        Material material = getMaterialById(id);

        if (updatedMaterial.getMaterialName() == null || updatedMaterial.getMaterialName().trim().isEmpty()) {
            throw new IllegalArgumentException("Material name is required.");
        }

        // Validate Unique Name against other materials
        Optional<Material> existing = materialRepository.findByMaterialNameIgnoreCase(updatedMaterial.getMaterialName().trim());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Material with name '" + updatedMaterial.getMaterialName().trim() + "' already exists in inventory.");
        }

        if (updatedMaterial.getUnitPrice() == null || updatedMaterial.getUnitPrice() < 1.0) {
            throw new IllegalArgumentException("Unit Cost must be greater than or equal to 1.");
        }
        if (updatedMaterial.getCurrentStock() == null || updatedMaterial.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Current in-stock quantity cannot be negative.");
        }
        if (updatedMaterial.getReorderLevel() == null || updatedMaterial.getReorderLevel() < 1) {
            throw new IllegalArgumentException("Reorder Threshold must be greater than or equal to 1.");
        }

        material.setMaterialName(updatedMaterial.getMaterialName().trim());
        material.setCategory(updatedMaterial.getCategory());
        material.setUnit(updatedMaterial.getUnit());
        material.setUnitPrice(updatedMaterial.getUnitPrice());
        material.setCurrentStock(updatedMaterial.getCurrentStock());
        material.setReorderLevel(updatedMaterial.getReorderLevel());
        if (updatedMaterial.getStatus() != null && !updatedMaterial.getStatus().trim().isEmpty()) {
            material.setStatus(updatedMaterial.getStatus());
        }
        return materialRepository.save(material);
    }

    public void deleteMaterial(Long id) {
        Material material = getMaterialById(id);

        // Soft Delete Check: If material has transaction history in material_issues, deactivate it
        List<MaterialIssue> issues = materialIssueRepository.findByMaterialId(id);
        if (!issues.isEmpty()) {
            material.setStatus("INACTIVE");
            materialRepository.save(material);
            WarehouseSessionManager.getInstance().logActivity(
                "Material '" + material.getMaterialName() + "' (" + material.getCustomId() + ") deactivated (Set INACTIVE) because it has " + issues.size() + " issue transaction records."
            );
        } else {
            materialRepository.deleteById(id);
            WarehouseSessionManager.getInstance().logActivity(
                "Material '" + material.getMaterialName() + "' permanently removed from warehouse."
            );
        }
    }

    // ==========================================
    // MATERIAL ISSUES (DISBURSEMENTS) CRUD
    // ==========================================

    @Transactional
    public MaterialIssue issueMaterial(MaterialIssue issue) {
        if (issue.getQuantityIssued() == null || issue.getQuantityIssued() < 1) {
            throw new IllegalArgumentException("Quantity to issue must be greater than or equal to 1.");
        }

        Material material = getMaterialById(issue.getMaterialId());

        if ("INACTIVE".equalsIgnoreCase(material.getStatus())) {
            throw new IllegalArgumentException("Cannot issue inactive material '" + material.getMaterialName() + "'. Please activate the material first.");
        }

        if (material.getCurrentStock() < issue.getQuantityIssued()) {
            throw new IllegalArgumentException("Insufficient stock! Available: " + material.getCurrentStock() + 
                    " " + material.getUnit() + ", Requested: " + issue.getQuantityIssued());
        }

        // Deduct from stock (note: currentStock can become 0 after issuing)
        material.setCurrentStock(material.getCurrentStock() - issue.getQuantityIssued());
        materialRepository.save(material);

        MaterialIssue saved = materialIssueRepository.save(issue);

        // Singleton pattern call to record material disbursement
        WarehouseSessionManager.getInstance().recordDisbursement(saved.getMaterialId(), saved.getQuantityIssued());

        return saved;
    }

    public List<String> getWarehouseAuditLogs() {
        return WarehouseSessionManager.getInstance().getAuditLogs();
    }

    public List<MaterialIssue> getAllMaterialIssues() {
        return materialIssueRepository.findAll();
    }

    public List<MaterialIssue> getMaterialIssuesByProject(Long projectId) {
        return materialIssueRepository.findByProjectId(projectId);
    }

    @Transactional
    public MaterialIssue updateMaterialIssue(Long id, MaterialIssue updatedIssue) {
        Optional<MaterialIssue> opt = materialIssueRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RuntimeException("Material issue record not found with ID: " + id);
        }
        if (updatedIssue.getQuantityIssued() == null || updatedIssue.getQuantityIssued() < 1) {
            throw new IllegalArgumentException("Quantity to issue must be greater than or equal to 1.");
        }

        MaterialIssue existingIssue = opt.get();
        Material material = getMaterialById(existingIssue.getMaterialId());

        // Revert old quantity to stock
        material.setCurrentStock(material.getCurrentStock() + existingIssue.getQuantityIssued());

        // Validate and deduct new quantity
        if (material.getCurrentStock() < updatedIssue.getQuantityIssued()) {
            throw new IllegalArgumentException("Insufficient stock for update! Available: " + material.getCurrentStock());
        }
        material.setCurrentStock(material.getCurrentStock() - updatedIssue.getQuantityIssued());
        materialRepository.save(material);

        existingIssue.setIssuedTo(updatedIssue.getIssuedTo());
        existingIssue.setQuantityIssued(updatedIssue.getQuantityIssued());
        existingIssue.setIssueDate(updatedIssue.getIssueDate());
        existingIssue.setNotes(updatedIssue.getNotes());

        return materialIssueRepository.save(existingIssue);
    }

    @Transactional
    public void deleteMaterialIssue(Long id) {
        Optional<MaterialIssue> opt = materialIssueRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RuntimeException("Material issue record not found with ID: " + id);
        }
        MaterialIssue issue = opt.get();

        // Restore stock upon cancellation (even if material is marked INACTIVE)
        Material material = getMaterialById(issue.getMaterialId());
        material.setCurrentStock(material.getCurrentStock() + issue.getQuantityIssued());
        materialRepository.save(material);

        // Log cancellation in Singleton
        WarehouseSessionManager.getInstance().logActivity(
            "Material Issue #" + id + " cancelled. Returned " + issue.getQuantityIssued() + " " + material.getUnit() + 
            " to '" + material.getMaterialName() + "' (" + material.getCustomId() + "). New stock: " + material.getCurrentStock() + 
            " [Status: " + material.getStatus() + "]"
        );

        materialIssueRepository.deleteById(id);
    }

    // Stock Removal / Physical Disposal
    @Transactional
    public Material adjustStockRemoval(Long materialId, int quantityToRemove, String reason, String notes) {
        Material material = getMaterialById(materialId);
        if (quantityToRemove < 1) {
            throw new IllegalArgumentException("Quantity to remove must be greater than or equal to 1.");
        }
        if (material.getCurrentStock() < quantityToRemove) {
            throw new IllegalArgumentException("Insufficient stock to remove! Available in-stock: " + material.getCurrentStock());
        }

        material.setCurrentStock(material.getCurrentStock() - quantityToRemove);
        materialRepository.save(material);

        WarehouseSessionManager.getInstance().logActivity(
            "Stock Removal/Disposal (" + (reason != null ? reason : "Other") + ") -> Material: " + 
            material.getMaterialName() + " (" + material.getCustomId() + "), Removed: " + quantityToRemove + 
            ", Remaining Stock: " + material.getCurrentStock() + (notes != null && !notes.trim().isEmpty() ? " | Notes: " + notes : "")
        );

        return material;
    }
}
