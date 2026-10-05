// ====================================================================
// Member 3: Bandara R.A.H.G.D. (IT25101722)
// Module: Material & Inventory Management (CRUD)
// ====================================================================

// ==========================================
// 1. MATERIALS INVENTORY CRUD
// ==========================================

async function loadMaterials() {
    const tbody = document.getElementById('materials-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/inventory/materials');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load materials');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No material stock found. Click "+ Add Material" to add items.</td></tr>';
            return;
        }

        list.forEach(m => {
            const tr = document.createElement('tr');
            const isLowStock = m.currentStock <= m.reorderLevel;
            const stockBadge = isLowStock 
                ? `<span class="badge badge-low-stock">${m.currentStock} ${m.unit} (LOW STOCK)</span>`
                : `<span class="badge bg-success">${m.currentStock} ${m.unit}</span>`;
            const statusBadge = (m.status === 'INACTIVE')
                ? `<span class="badge bg-secondary">INACTIVE</span>`
                : `<span class="badge bg-info text-dark">ACTIVE</span>`;
            const customId = m.customId || ('MID' + String(m.id).padStart(3, '0'));

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td><strong>${m.materialName}</strong></td>
                <td><span class="badge bg-secondary">${m.category}</span></td>
                <td>${formatCurrency(m.unitPrice)} / ${m.unit}</td>
                <td>${stockBadge}</td>
                <td>Reorder at: ${m.reorderLevel} ${m.unit}</td>
                <td>${statusBadge}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditMaterialModal(${m.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteMaterial(${m.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Update Material Dropdowns with Active Materials only
        updateMaterialDropdowns(list.filter(m => m.status !== 'INACTIVE'));
    } catch (err) {
        showToast('Failed to load materials: ' + err.message, 'error');
    }
}

function updateMaterialDropdowns(materials) {
    const selects = document.querySelectorAll('.material-selector');
    selects.forEach(sel => {
        const currentVal = sel.value;
        sel.innerHTML = '<option value="">-- Select Material Item --</option>';
        materials.forEach(m => {
            const customId = m.customId || ('MID' + String(m.id).padStart(3, '0'));
            sel.innerHTML += `<option value="${m.id}" data-unit="${m.unit}" data-stock="${m.currentStock}">${customId} - ${m.materialName} (In Stock: ${m.currentStock} ${m.unit})</option>`;
        });
        if (currentVal) sel.value = currentVal;
    });
}

function openNewMaterialModal() {
    document.getElementById('material-id').value = '';
    document.getElementById('material-form').reset();
    document.getElementById('materialModalTitle').innerText = 'Add New Inventory Material';
    new bootstrap.Modal(document.getElementById('materialModal')).show();
}

async function openEditMaterialModal(id) {
    try {
        const res = await fetch(`/api/inventory/materials/${id}`);
        const m = await res.json();

        document.getElementById('material-id').value = m.id;
        document.getElementById('material-name').value = m.materialName;
        document.getElementById('material-category').value = m.category;
        document.getElementById('material-unit').value = m.unit;
        document.getElementById('material-price').value = m.unitPrice;
        document.getElementById('material-stock').value = m.currentStock;
        document.getElementById('material-reorder').value = m.reorderLevel;

        document.getElementById('materialModalTitle').innerText = 'Edit Material Details';
        new bootstrap.Modal(document.getElementById('materialModal')).show();
    } catch (err) {
        showToast('Failed to fetch material: ' + err.message, 'error');
    }
}

async function saveMaterial(event) {
    event.preventDefault();
    const id = document.getElementById('material-id').value;
    const name = document.getElementById('material-name').value.trim();
    const category = document.getElementById('material-category').value;
    const unit = document.getElementById('material-unit').value.trim();
    const unitPrice = parseFloat(document.getElementById('material-price').value);
    const currentStock = parseInt(document.getElementById('material-stock').value);
    const reorderLevel = parseInt(document.getElementById('material-reorder').value);

    // Human-style client validations
    if (!name) {
        showToast('Material description is required.', 'error');
        return;
    }
    if (isNaN(unitPrice) || unitPrice < 1) {
        showToast('Unit price must be at least 1 LKR.', 'error');
        return;
    }
    if (isNaN(currentStock) || currentStock < 1) {
        showToast('Initial stock level must be at least 1.', 'error');
        return;
    }
    if (isNaN(reorderLevel) || reorderLevel < 1) {
        showToast('Reorder threshold must be at least 1.', 'error');
        return;
    }

    const data = {
        materialName: name,
        category: category,
        unit: unit,
        unitPrice: unitPrice,
        currentStock: currentStock,
        reorderLevel: reorderLevel
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/inventory/materials/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/inventory/materials', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save material');
        }

        showToast(id ? 'Material updated!' : 'Material added to inventory!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('materialModal'));
        if (modal) modal.hide();
        loadMaterials();
    } catch (err) {
        showToast('Error saving material: ' + err.message, 'error');
    }
}

async function deleteMaterial(id) {
    if (!confirm('Are you sure you want to remove this material from catalog?')) return;
    try {
        const res = await fetch(`/api/inventory/materials/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to delete material');
        }
        showToast('Material updated/removed successfully!');
        loadMaterials();
    } catch (err) {
        showToast('Failed to delete material: ' + err.message, 'error');
    }
}

// ==========================================
// 2. MATERIAL ISSUES CRUD
// ==========================================

async function loadMaterialIssues() {
    const tbody = document.getElementById('issues-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/inventory/issues');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load material issues');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No materials issued yet.</td></tr>';
            return;
        }

        list.forEach(i => {
            const tr = document.createElement('tr');
            const customId = i.customId || ('ISU' + String(i.id).padStart(3, '0'));
            const customMatId = 'MID' + String(i.materialId).padStart(3, '0');
            const customProjId = i.projectId ? ('PID' + String(i.projectId).padStart(3, '0')) : 'N/A';

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td>Material ${customMatId}</td>
                <td>${customProjId}</td>
                <td><strong>${i.quantityIssued}</strong></td>
                <td>${i.issuedTo}</td>
                <td>${i.issueDate}</td>
                <td>
                    <button class="btn btn-sm btn-outline-danger" onclick="cancelMaterialIssue(${i.id})">Cancel (Restore Stock)</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load material issues: ' + err.message, 'error');
    }
}

function openIssueMaterialModal() {
    document.getElementById('issue-form').reset();
    document.getElementById('issue-date').value = new Date().toISOString().split('T')[0];
    new bootstrap.Modal(document.getElementById('issueModal')).show();
}

async function saveMaterialIssue(event) {
    event.preventDefault();
    const materialSelect = document.getElementById('issue-material-id');
    const materialId = materialSelect.value;
    const quantityIssued = parseInt(document.getElementById('issue-quantity').value);
    const issuedTo = document.getElementById('issue-recipient').value.trim();

    if (!materialId) {
        showToast('Please select a material to issue.', 'error');
        return;
    }
    if (isNaN(quantityIssued) || quantityIssued < 1) {
        showToast('Quantity to issue must be at least 1.', 'error');
        return;
    }
    if (!issuedTo) {
        showToast('Recipient / site foreman name is required.', 'error');
        return;
    }

    // Check available stock against selected option dataset
    const selectedOption = materialSelect.options[materialSelect.selectedIndex];
    const availableStock = selectedOption ? parseInt(selectedOption.dataset.stock) : 0;
    if (quantityIssued > availableStock) {
        showToast(`Requested quantity (${quantityIssued}) exceeds available stock (${availableStock}).`, 'error');
        return;
    }

    const data = {
        materialId: parseInt(materialId),
        projectId: document.getElementById('issue-project-id').value || null,
        issuedTo: issuedTo,
        quantityIssued: quantityIssued,
        issueDate: document.getElementById('issue-date').value,
        notes: document.getElementById('issue-notes').value
    };

    try {
        const res = await fetch('/api/inventory/issues', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            const errJson = await res.json().catch(() => ({}));
            throw new Error(errJson.message || errJson.error || 'Failed to issue material');
        }

        showToast('Material issued to site and stock deducted successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('issueModal'));
        if (modal) modal.hide();
        loadMaterials();
        loadMaterialIssues();
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function cancelMaterialIssue(id) {
    if (!confirm('Are you sure you want to cancel this issue record and restore materials back to stock?')) return;
    try {
        const res = await fetch(`/api/inventory/issues/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to cancel issue');
        }
        showToast('Issue cancelled and stock restored to inventory!');
        loadMaterials();
        loadMaterialIssues();
    } catch (err) {
        showToast('Failed to cancel issue: ' + err.message, 'error');
    }
}

// ==========================================
// 3. STOCK REMOVAL & DISPOSAL
// ==========================================

function openStockRemovalModal() {
    const form = document.getElementById('stock-removal-form');
    if (form) form.reset();
    new bootstrap.Modal(document.getElementById('stockRemovalModal')).show();
}

async function saveStockRemoval(event) {
    event.preventDefault();
    const materialId = document.getElementById('removal-material-id').value;
    const quantity = parseInt(document.getElementById('removal-quantity').value);
    const reason = document.getElementById('removal-reason').value;
    const notes = document.getElementById('removal-notes').value.trim();

    if (!materialId) {
        showToast('Please select a material.', 'error');
        return;
    }
    if (isNaN(quantity) || quantity < 1) {
        showToast('Quantity to remove must be at least 1.', 'error');
        return;
    }

    try {
        const fullReason = notes ? `${reason} (${notes})` : reason;
        const res = await fetch(`/api/inventory/materials/${materialId}/adjust-stock`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity, reason: fullReason })
        });

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to adjust stock');
        }

        showToast('Stock removal processed and inventory adjusted!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('stockRemovalModal'));
        if (modal) modal.hide();
        loadMaterials();
    } catch (err) {
        showToast('Stock Removal Error: ' + err.message, 'error');
    }
}

// ==========================================
// 4. INVENTORY & VALUATION REPORT GENERATOR
// ==========================================

async function printInventoryReport() {
    try {
        const [matRes, issueRes] = await Promise.all([
            fetch('/api/inventory/materials'),
            fetch('/api/inventory/issues')
        ]);

        const materials = await matRes.json();
        const issues = await issueRes.json();

        const printWindow = window.open('', '_blank');
        if (!printWindow) {
            showToast('Pop-up blocked. Please allow pop-ups to print the report.', 'error');
            return;
        }

        const now = new Date().toLocaleString();
        const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
        const generatorName = currentUser ? `${currentUser.fullName} (${currentUser.role})` : 'Bandara R.A.H.G.D. (Storekeeper)';

        let totalInventoryValue = 0;
        let materialsRows = materials.map(m => {
            const customId = m.customId || ('MID' + String(m.id).padStart(3, '0'));
            const itemValue = (m.unitPrice || 0) * (m.currentStock || 0);
            totalInventoryValue += itemValue;
            const isLow = m.currentStock <= m.reorderLevel;

            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>${m.materialName}</td>
                    <td>${m.category}</td>
                    <td>${formatCurrency(m.unitPrice)} / ${m.unit}</td>
                    <td><strong>${m.currentStock} ${m.unit}</strong> ${isLow ? '<span style="color: #dc2626; font-weight: bold;">(LOW)</span>' : ''}</td>
                    <td>${formatCurrency(itemValue)}</td>
                    <td>${m.reorderLevel} ${m.unit}</td>
                    <td><strong>${m.status || 'ACTIVE'}</strong></td>
                </tr>
            `;
        }).join('');

        let issuesRows = issues.map(i => {
            const customId = i.customId || ('ISU' + String(i.id).padStart(3, '0'));
            const customMatId = 'MID' + String(i.materialId).padStart(3, '0');
            const customProjId = i.projectId ? ('PID' + String(i.projectId).padStart(3, '0')) : 'N/A';

            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>Material ${customMatId}</td>
                    <td>${customProjId}</td>
                    <td><strong>${i.quantityIssued}</strong></td>
                    <td>${i.issuedTo}</td>
                    <td>${i.issueDate}</td>
                    <td>${i.notes || '-'}</td>
                </tr>
            `;
        }).join('');

        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>ApexBuild - Inventory & Warehouse Valuation Report</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 30px; color: #1e293b; line-height: 1.5; }
                    .header { display: flex; justify-content: space-between; border-bottom: 2px solid #f59e0b; padding-bottom: 15px; margin-bottom: 25px; }
                    .title { font-size: 24px; font-weight: bold; color: #0f172a; }
                    .meta { font-size: 13px; color: #64748b; }
                    .summary-card { background: #fef3c7; border: 1px solid #fde68a; padding: 12px 18px; border-radius: 6px; margin-bottom: 20px; font-weight: 600; color: #92400e; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 25px; font-size: 13px; }
                    th, td { border: 1px solid #cbd5e1; padding: 9px 12px; text-align: left; }
                    th { background-color: #f1f5f9; color: #334155; font-weight: 600; }
                    h3 { color: #d97706; margin-top: 25px; font-size: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; }
                    .footer { margin-top: 40px; font-size: 12px; color: #94a3b8; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 15px; }
                    @media print { .no-print { display: none; } }
                </style>
            </head>
            <body>
                <div class="no-print" style="margin-bottom: 15px;">
                    <button onclick="window.print()" style="padding: 8px 16px; background: #f59e0b; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Print / Save as PDF</button>
                    <button onclick="window.close()" style="padding: 8px 16px; background: #64748b; color: white; border: none; border-radius: 4px; cursor: pointer;">Close</button>
                </div>
                <div class="header">
                    <div>
                        <div class="title">🏗️ ApexBuild Construction ERP</div>
                        <div style="font-weight: 600; color: #d97706;">Warehouse Stock & Material Valuation Statement</div>
                    </div>
                    <div class="meta" style="text-align: right;">
                        <div>Generated: ${now}</div>
                        <div>Generated By: ${generatorName}</div>
                        <div>Module: Warehouse & Inventory Control</div>
                    </div>
                </div>

                <div class="summary-card">
                    Total In-Stock Catalog Valuation: ${formatCurrency(totalInventoryValue)} &bull; Total Material SKUs: ${materials.length}
                </div>

                <h3>Material Catalog & In-Stock Valuation (${materials.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Material ID</th><th>Description</th><th>Category</th><th>Unit Price</th><th>In-Stock</th><th>Stock Valuation</th><th>Threshold</th><th>Status</th>
                        </tr>
                    </thead>
                    <tbody>${materialsRows}</tbody>
                </table>

                <h3>Disbursement & Site Issue Logs (${issues.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Issue ID</th><th>Material Ref</th><th>Project Ref</th><th>Qty Issued</th><th>Issued To</th><th>Issue Date</th><th>Notes</th>
                        </tr>
                    </thead>
                    <tbody>${issuesRows}</tbody>
                </table>

                <div class="footer">ApexBuild Enterprise Construction Management System &bull; Confidential Internal Report</div>
            </body>
            </html>
        `);
        printWindow.document.close();
    } catch (err) {
        showToast('Failed to generate inventory report: ' + err.message, 'error');
    }
}
