// ====================================================================
// Member 4: Vaishnavy S. (IT25101549)
// Module: Supplier & Procurement Management (CRUD)
// ====================================================================

// ==========================================
// 1. SUPPLIERS DIRECTORY CRUD
// ==========================================

async function loadSuppliers() {
    const tbody = document.getElementById('suppliers-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/procurement/suppliers');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load suppliers');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No suppliers registered. Click "+ Add Supplier" to register one.</td></tr>';
            return;
        }

        list.forEach(s => {
            const tr = document.createElement('tr');
            let statusBadge = '<span class="badge bg-success">ACTIVE</span>';
            if (s.status === 'INACTIVE') statusBadge = '<span class="badge bg-secondary">INACTIVE</span>';
            if (s.status === 'BLACKLISTED') statusBadge = '<span class="badge bg-danger">BLACKLISTED</span>';

            const customId = s.customId || ('SID' + String(s.id).padStart(3, '0'));

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td><strong>${s.companyName}</strong></td>
                <td>${s.contactPerson}</td>
                <td>
                    <span class="fw-semibold text-light">${s.phone}</span><br>
                    <small class="text-info">${s.email}</small>
                </td>
                <td><span class="badge bg-info text-dark">${s.supplyCategory}</span></td>
                <td>${statusBadge}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditSupplierModal(${s.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteSupplier(${s.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        updateSupplierDropdowns(list);
    } catch (err) {
        showToast('Failed to load suppliers: ' + err.message, 'error');
    }
}

function updateSupplierDropdowns(suppliers) {
    const selects = document.querySelectorAll('.supplier-selector');
    selects.forEach(sel => {
        const currentVal = sel.value;
        sel.innerHTML = '<option value="">-- Select Preferred Supplier --</option>';
        suppliers.forEach(s => {
            const customId = s.customId || ('SID' + String(s.id).padStart(3, '0'));
            sel.innerHTML += `<option value="${s.id}">${customId} - ${s.companyName} (${s.supplyCategory})</option>`;
        });
        if (currentVal) sel.value = currentVal;
    });
}

function openNewSupplierModal() {
    document.getElementById('supplier-id').value = '';
    document.getElementById('supplier-form').reset();
    document.getElementById('supplierModalTitle').innerText = 'Register New Material Supplier';
    new bootstrap.Modal(document.getElementById('supplierModal')).show();
}

async function openEditSupplierModal(id) {
    try {
        const res = await fetch(`/api/procurement/suppliers/${id}`);
        const s = await res.json();

        document.getElementById('supplier-id').value = s.id;
        document.getElementById('supplier-company').value = s.companyName;
        document.getElementById('supplier-contact').value = s.contactPerson;
        document.getElementById('supplier-phone').value = s.phone;
        document.getElementById('supplier-email').value = s.email;
        document.getElementById('supplier-address').value = s.address || '';
        document.getElementById('supplier-category').value = s.supplyCategory;
        document.getElementById('supplier-status').value = s.status;

        document.getElementById('supplierModalTitle').innerText = 'Edit Supplier Profile';
        new bootstrap.Modal(document.getElementById('supplierModal')).show();
    } catch (err) {
        showToast('Failed to fetch supplier: ' + err.message, 'error');
    }
}

async function saveSupplier(event) {
    event.preventDefault();
    const id = document.getElementById('supplier-id').value;
    const company = document.getElementById('supplier-company').value.trim();
    const contact = document.getElementById('supplier-contact').value.trim();
    const phone = document.getElementById('supplier-phone').value.trim();
    const email = document.getElementById('supplier-email').value.trim();

    // Client-side human validations
    if (!company) {
        showToast('Company name is required.', 'error');
        return;
    }
    if (!contact) {
        showToast('Contact person is required.', 'error');
        return;
    }
    // Exactly 10 digits validation
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(phone)) {
        showToast('Contact phone number must be exactly 10 digits (e.g. 0771234567).', 'error');
        return;
    }

    const data = {
        companyName: company,
        contactPerson: contact,
        phone: phone,
        email: email,
        address: document.getElementById('supplier-address').value,
        supplyCategory: document.getElementById('supplier-category').value,
        status: document.getElementById('supplier-status').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/procurement/suppliers/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/procurement/suppliers', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save supplier');
        }

        showToast(id ? 'Supplier updated!' : 'Supplier registered successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('supplierModal'));
        if (modal) modal.hide();
        loadSuppliers();
    } catch (err) {
        showToast('Error saving supplier: ' + err.message, 'error');
    }
}

async function deleteSupplier(id) {
    if (!confirm('Are you sure you want to remove this supplier?')) return;
    try {
        const res = await fetch(`/api/procurement/suppliers/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to delete supplier');
        }
        showToast('Supplier deleted successfully!');
        loadSuppliers();
    } catch (err) {
        showToast('Failed to delete supplier: ' + err.message, 'error');
    }
}

// ==========================================
// 2. PURCHASE REQUESTS CRUD
// ==========================================

async function loadPurchaseRequests() {
    const tbody = document.getElementById('requests-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/procurement/requests');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load purchase requests');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No purchase orders created yet.</td></tr>';
            return;
        }

        list.forEach(r => {
            const tr = document.createElement('tr');
            let statusBadge = '<span class="badge bg-warning text-dark">PENDING</span>';
            if (r.status === 'APPROVED') statusBadge = '<span class="badge bg-info text-dark">APPROVED</span>';
            if (r.status === 'DELIVERED') statusBadge = '<span class="badge bg-success">DELIVERED</span>';
            if (r.status === 'REJECTED') statusBadge = '<span class="badge bg-danger">REJECTED</span>';

            const customId = r.customId || ('PO' + String(r.id).padStart(3, '0'));

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td><strong>${r.materialName}</strong></td>
                <td>${r.quantity}</td>
                <td>${formatCurrency(r.estimatedCost)}</td>
                <td>${statusBadge}</td>
                <td>${r.requestDate}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditRequestModal(${r.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deletePurchaseRequest(${r.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load purchase requests: ' + err.message, 'error');
    }
}

function openNewPurchaseRequestModal() {
    document.getElementById('request-id').value = '';
    document.getElementById('request-form').reset();
    document.getElementById('request-date').value = new Date().toISOString().split('T')[0];
    document.getElementById('requestModalTitle').innerText = 'Create Purchase Request';
    new bootstrap.Modal(document.getElementById('requestModal')).show();
}

async function openEditRequestModal(id) {
    try {
        const res = await fetch(`/api/procurement/requests/${id}`);
        const r = await res.json();

        document.getElementById('request-id').value = r.id;
        document.getElementById('request-supplier-id').value = r.supplierId || '';
        document.getElementById('request-material-name').value = r.materialName;
        document.getElementById('request-quantity').value = r.quantity;
        document.getElementById('request-cost').value = r.estimatedCost;
        document.getElementById('request-status').value = r.status;
        document.getElementById('request-date').value = r.requestDate;
        document.getElementById('request-notes').value = r.notes || '';

        document.getElementById('requestModalTitle').innerText = 'Edit Purchase Request';
        new bootstrap.Modal(document.getElementById('requestModal')).show();
    } catch (err) {
        showToast('Failed to fetch request details: ' + err.message, 'error');
    }
}

async function savePurchaseRequest(event) {
    event.preventDefault();
    const id = document.getElementById('request-id').value;
    const materialName = document.getElementById('request-material-name').value.trim();
    const quantity = parseInt(document.getElementById('request-quantity').value);
    const estimatedCost = parseFloat(document.getElementById('request-cost').value);

    // Human client validations
    if (!materialName) {
        showToast('Material description is required.', 'error');
        return;
    }
    if (isNaN(quantity) || quantity < 1) {
        showToast('Order quantity must be at least 1.', 'error');
        return;
    }
    if (isNaN(estimatedCost) || estimatedCost <= 0) {
        showToast('Estimated cost must be greater than 0 LKR.', 'error');
        return;
    }

    const data = {
        supplierId: document.getElementById('request-supplier-id').value || null,
        materialName: materialName,
        quantity: quantity,
        estimatedCost: estimatedCost,
        status: document.getElementById('request-status').value,
        requestDate: document.getElementById('request-date').value,
        notes: document.getElementById('request-notes').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/procurement/requests/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/procurement/requests', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save purchase request');
        }

        showToast(id ? 'Purchase order updated!' : 'Purchase request sent!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('requestModal'));
        if (modal) modal.hide();
        loadPurchaseRequests();
    } catch (err) {
        showToast('Error saving request: ' + err.message, 'error');
    }
}

async function deletePurchaseRequest(id) {
    if (!confirm('Are you sure you want to cancel this purchase request?')) return;
    try {
        const res = await fetch(`/api/procurement/requests/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to delete request');
        }
        showToast('Purchase request cancelled!');
        loadPurchaseRequests();
    } catch (err) {
        showToast('Failed to delete request: ' + err.message, 'error');
    }
}

// ==========================================
// 3. PROCUREMENT & SUPPLY CHAIN REPORT
// ==========================================

async function printProcurementReport() {
    try {
        const [supRes, poRes] = await Promise.all([
            fetch('/api/procurement/suppliers'),
            fetch('/api/procurement/requests')
        ]);

        const suppliers = await supRes.json();
        const requests = await poRes.json();

        const printWindow = window.open('', '_blank');
        if (!printWindow) {
            showToast('Pop-up blocked. Please allow pop-ups to print the report.', 'error');
            return;
        }

        const now = new Date().toLocaleString();
        const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
        const generatorName = currentUser ? `${currentUser.fullName} (${currentUser.role})` : 'Vaishnavy S. (Procurement Officer)';

        let totalPOValue = 0;
        let poRows = requests.map(r => {
            const customId = r.customId || ('PO' + String(r.id).padStart(3, '0'));
            totalPOValue += (r.estimatedCost || 0);

            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>${r.materialName}</td>
                    <td>${r.supplierId ? 'SID' + String(r.supplierId).padStart(3, '0') : 'Open Market'}</td>
                    <td><strong>${r.quantity}</strong></td>
                    <td>${formatCurrency(r.estimatedCost)}</td>
                    <td><strong>${r.status}</strong></td>
                    <td>${r.requestDate}</td>
                </tr>
            `;
        }).join('');

        let supRows = suppliers.map(s => {
            const customId = s.customId || ('SID' + String(s.id).padStart(3, '0'));
            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>${s.companyName}</td>
                    <td>${s.contactPerson}</td>
                    <td>${s.phone} / ${s.email}</td>
                    <td>${s.supplyCategory}</td>
                    <td><strong>${s.status}</strong></td>
                </tr>
            `;
        }).join('');

        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>ApexBuild - Procurement & Vendor Purchase Report</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 30px; color: #1e293b; line-height: 1.5; }
                    .header { display: flex; justify-content: space-between; border-bottom: 2px solid #8b5cf6; padding-bottom: 15px; margin-bottom: 25px; }
                    .title { font-size: 24px; font-weight: bold; color: #0f172a; }
                    .meta { font-size: 13px; color: #64748b; }
                    .summary-card { background: #ede9fe; border: 1px solid #ddd6fe; padding: 12px 18px; border-radius: 6px; margin-bottom: 20px; font-weight: 600; color: #5b21b6; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 25px; font-size: 13px; }
                    th, td { border: 1px solid #cbd5e1; padding: 9px 12px; text-align: left; }
                    th { background-color: #f1f5f9; color: #334155; font-weight: 600; }
                    h3 { color: #6d28d9; margin-top: 25px; font-size: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; }
                    .footer { margin-top: 40px; font-size: 12px; color: #94a3b8; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 15px; }
                    @media print { .no-print { display: none; } }
                </style>
            </head>
            <body>
                <div class="no-print" style="margin-bottom: 15px;">
                    <button onclick="window.print()" style="padding: 8px 16px; background: #8b5cf6; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Print / Save as PDF</button>
                    <button onclick="window.close()" style="padding: 8px 16px; background: #64748b; color: white; border: none; border-radius: 4px; cursor: pointer;">Close</button>
                </div>
                <div class="header">
                    <div>
                        <div class="title">🏗️ ApexBuild Construction ERP</div>
                        <div style="font-weight: 600; color: #6d28d9;">Supply Chain & Procurement Orders Summary</div>
                    </div>
                    <div class="meta" style="text-align: right;">
                        <div>Generated: ${now}</div>
                        <div>Generated By: ${generatorName}</div>
                        <div>Module: Vendor Procurement & Inbound Supply</div>
                    </div>
                </div>

                <div class="summary-card">
                    Total Estimated Orders Value: ${formatCurrency(totalPOValue)} &bull; Total Purchase Orders: ${requests.length} &bull; Registered Suppliers: ${suppliers.length}
                </div>

                <h3>Purchase Orders & Inbound Requisitions (${requests.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>PO Number</th><th>Material Requested</th><th>Supplier Ref</th><th>Order Qty</th><th>Estimated Cost</th><th>Order Status</th><th>Date</th>
                        </tr>
                    </thead>
                    <tbody>${poRows}</tbody>
                </table>

                <h3>Approved Vendor & Supplier Directory (${suppliers.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Supplier ID</th><th>Company Name</th><th>Contact Person</th><th>Contact Info</th><th>Supply Scope</th><th>Status</th>
                        </tr>
                    </thead>
                    <tbody>${supRows}</tbody>
                </table>

                <div class="footer">ApexBuild Enterprise Construction Management System &bull; Confidential Internal Report</div>
            </body>
            </html>
        `);
        printWindow.document.close();
    } catch (err) {
        showToast('Failed to generate procurement report: ' + err.message, 'error');
    }
}
