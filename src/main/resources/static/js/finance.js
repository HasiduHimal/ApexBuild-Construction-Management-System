// ====================================================================
// Member 5: Pahanmi S.B.G. (IT25103433)
// Module: Financial Management & Quotation Engineering (CRUD)
// ====================================================================

// ==========================================
// 1. EXPENSES LEDGER CRUD
// ==========================================

async function loadExpenses() {
    const tbody = document.getElementById('expenses-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/finance/expenses');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load expenses');
        }

        tbody.innerHTML = '';
        let totalExpense = 0;

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No expense records found. Click "+ Record Expense" to log one.</td></tr>';
            const statElem = document.getElementById('total-expenses-stat');
            if (statElem) statElem.innerText = 'Rs. 0.00';
            return;
        }

        list.forEach(e => {
            totalExpense += parseFloat(e.amount) || 0;
            const tr = document.createElement('tr');
            const customId = e.customId || ('EXP' + String(e.id).padStart(3, '0'));
            const customProjId = e.projectId ? ('PID' + String(e.projectId).padStart(3, '0')) : 'General';

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td>Project ${customProjId}</td>
                <td><strong>${e.expenseTitle}</strong></td>
                <td><span class="badge bg-secondary">${e.category}</span></td>
                <td class="text-danger fw-bold">${formatCurrency(e.amount)}</td>
                <td>${e.expenseDate}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditExpenseModal(${e.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteExpense(${e.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        const statElem = document.getElementById('total-expenses-stat');
        if (statElem) statElem.innerText = formatCurrency(totalExpense);
    } catch (err) {
        showToast('Failed to load expenses: ' + err.message, 'error');
    }
}

function openNewExpenseModal() {
    document.getElementById('expense-id').value = '';
    document.getElementById('expense-form').reset();
    document.getElementById('expense-date').value = new Date().toISOString().split('T')[0];
    document.getElementById('expenseModalTitle').innerText = 'Record Project Expense';
    new bootstrap.Modal(document.getElementById('expenseModal')).show();
}

async function openEditExpenseModal(id) {
    try {
        const res = await fetch(`/api/finance/expenses/${id}`);
        const e = await res.json();

        document.getElementById('expense-id').value = e.id;
        document.getElementById('expense-project-id').value = e.projectId;
        document.getElementById('expense-title').value = e.expenseTitle;
        document.getElementById('expense-category').value = e.category;
        document.getElementById('expense-amount').value = e.amount;
        document.getElementById('expense-date').value = e.expenseDate;
        document.getElementById('expense-description').value = e.description || '';

        document.getElementById('expenseModalTitle').innerText = 'Edit Expense Record';
        new bootstrap.Modal(document.getElementById('expenseModal')).show();
    } catch (err) {
        showToast('Failed to load expense: ' + err.message, 'error');
    }
}

async function saveExpense(event) {
    event.preventDefault();
    const id = document.getElementById('expense-id').value;
    const title = document.getElementById('expense-title').value.trim();
    const amount = parseFloat(document.getElementById('expense-amount').value);
    const projectId = document.getElementById('expense-project-id').value;

    // Human client validations
    if (!title) {
        showToast('Expense title is required.', 'error');
        return;
    }
    if (isNaN(amount) || amount <= 0) {
        showToast('Expense amount must be greater than 0 LKR.', 'error');
        return;
    }
    if (!projectId) {
        showToast('Target project is required.', 'error');
        return;
    }

    const data = {
        projectId: parseInt(projectId),
        expenseTitle: title,
        category: document.getElementById('expense-category').value,
        amount: amount,
        expenseDate: document.getElementById('expense-date').value,
        description: document.getElementById('expense-description').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/finance/expenses/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/finance/expenses', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save expense');
        }

        showToast(id ? 'Expense voucher updated!' : 'Expense recorded successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('expenseModal'));
        if (modal) modal.hide();
        loadExpenses();
    } catch (err) {
        showToast('Error saving expense: ' + err.message, 'error');
    }
}

async function deleteExpense(id) {
    if (!confirm('Are you sure you want to delete this expense voucher?')) return;
    try {
        const res = await fetch(`/api/finance/expenses/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to delete expense');
        }
        showToast('Expense voucher deleted!');
        loadExpenses();
    } catch (err) {
        showToast('Failed to delete expense: ' + err.message, 'error');
    }
}

// ==========================================
// 2. PROJECT QUOTATIONS CRUD
// ==========================================

async function loadQuotations() {
    const tbody = document.getElementById('quotations-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/finance/quotations');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load quotations');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No quotations generated yet. Click "+ Generate Quotation" to create one.</td></tr>';
            return;
        }

        list.forEach(q => {
            const tr = document.createElement('tr');
            let statusBadge = '<span class="badge bg-secondary">DRAFT</span>';
            if (q.status === 'SENT') statusBadge = '<span class="badge bg-info text-dark">SENT</span>';
            if (q.status === 'ACCEPTED') statusBadge = '<span class="badge bg-success">ACCEPTED</span>';
            if (q.status === 'REJECTED') statusBadge = '<span class="badge bg-danger">REJECTED</span>';

            const customId = q.customId || ('QUO' + String(q.id).padStart(3, '0'));

            tr.innerHTML = `
                <td><strong>${customId}</strong></td>
                <td><strong>${q.projectName}</strong></td>
                <td>${q.clientName}<br><small class="text-muted">${q.clientEmail}</small></td>
                <td>Labor: ${formatCurrency(q.laborCost)}<br>Material: ${formatCurrency(q.materialCost)}</td>
                <td class="fw-bold text-primary">${formatCurrency(q.totalAmount)}</td>
                <td>${statusBadge}</td>
                <td>${q.generatedDate}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditQuotationModal(${q.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteQuotation(${q.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load quotations: ' + err.message, 'error');
    }
}

function calculateQuotationTotal() {
    const labor = parseFloat(document.getElementById('quote-labor').value) || 0;
    const material = parseFloat(document.getElementById('quote-material').value) || 0;
    const overhead = parseFloat(document.getElementById('quote-overhead').value) || 0;
    const total = labor + material + overhead;
    document.getElementById('quote-total-display').value = formatCurrency(total);
}

function openNewQuotationModal() {
    document.getElementById('quote-id').value = '';
    document.getElementById('quote-form').reset();
    document.getElementById('quote-date').value = new Date().toISOString().split('T')[0];
    document.getElementById('quote-total-display').value = 'Rs. 0.00';
    document.getElementById('quoteModalTitle').innerText = 'Generate New Cost Quotation';
    new bootstrap.Modal(document.getElementById('quotationModal')).show();
}

async function openEditQuotationModal(id) {
    try {
        const res = await fetch(`/api/finance/quotations/${id}`);
        const q = await res.json();

        document.getElementById('quote-id').value = q.id;
        document.getElementById('quote-project-id').value = q.projectId || '';
        document.getElementById('quote-project-name').value = q.projectName;
        document.getElementById('quote-client-name').value = q.clientName;
        document.getElementById('quote-client-email').value = q.clientEmail;
        document.getElementById('quote-labor').value = q.laborCost;
        document.getElementById('quote-material').value = q.materialCost;
        document.getElementById('quote-overhead').value = q.overheadCost;
        document.getElementById('quote-status').value = q.status;
        document.getElementById('quote-date').value = q.generatedDate;

        calculateQuotationTotal();

        document.getElementById('quoteModalTitle').innerText = 'Edit Cost Quotation';
        new bootstrap.Modal(document.getElementById('quotationModal')).show();
    } catch (err) {
        showToast('Failed to fetch quotation: ' + err.message, 'error');
    }
}

async function saveQuotation(event) {
    event.preventDefault();
    const id = document.getElementById('quote-id').value;
    const projName = document.getElementById('quote-project-name').value.trim();
    const clientName = document.getElementById('quote-client-name').value.trim();
    const labor = parseFloat(document.getElementById('quote-labor').value);
    const material = parseFloat(document.getElementById('quote-material').value);
    const overhead = parseFloat(document.getElementById('quote-overhead').value);

    // Human client validations
    if (!projName) {
        showToast('Project title is required.', 'error');
        return;
    }
    if (!clientName) {
        showToast('Client name is required.', 'error');
        return;
    }
    if (isNaN(labor) || labor < 0 || isNaN(material) || material < 0 || isNaN(overhead) || overhead < 0) {
        showToast('Quotation cost components cannot be negative.', 'error');
        return;
    }
    if ((labor + material + overhead) <= 0) {
        showToast('Total quotation amount must be greater than 0 LKR.', 'error');
        return;
    }

    const data = {
        projectId: document.getElementById('quote-project-id').value || null,
        projectName: projName,
        clientName: clientName,
        clientEmail: document.getElementById('quote-client-email').value,
        laborCost: labor,
        materialCost: material,
        overheadCost: overhead,
        status: document.getElementById('quote-status').value,
        generatedDate: document.getElementById('quote-date').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/finance/quotations/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/finance/quotations', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save quotation');
        }

        showToast(id ? 'Quotation updated!' : 'Quotation generated successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('quotationModal'));
        if (modal) modal.hide();
        loadQuotations();
    } catch (err) {
        showToast('Error saving quotation: ' + err.message, 'error');
    }
}

async function deleteQuotation(id) {
    if (!confirm('Are you sure you want to discard this quotation?')) return;
    try {
        const res = await fetch(`/api/finance/quotations/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to delete quotation');
        }
        showToast('Quotation discarded!');
        loadQuotations();
    } catch (err) {
        showToast('Failed to delete quotation: ' + err.message, 'error');
    }
}

// ==========================================
// 3. FINANCIAL STATEMENT & REPORT GENERATOR
// ==========================================

async function printFinancialReport() {
    try {
        const [expRes, quoRes] = await Promise.all([
            fetch('/api/finance/expenses'),
            fetch('/api/finance/quotations')
        ]);

        const expenses = await expRes.json();
        const quotations = await quoRes.json();

        const printWindow = window.open('', '_blank');
        if (!printWindow) {
            showToast('Pop-up blocked. Please allow pop-ups to print the report.', 'error');
            return;
        }

        const now = new Date().toLocaleString();
        const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
        const generatorName = currentUser ? `${currentUser.fullName} (${currentUser.role})` : 'Pahanmi S.B.G. (Finance Officer)';

        let totalExpenses = 0;
        let expenseRows = expenses.map(e => {
            const customId = e.customId || ('EXP' + String(e.id).padStart(3, '0'));
            const customProjId = e.projectId ? ('PID' + String(e.projectId).padStart(3, '0')) : 'General';
            totalExpenses += (e.amount || 0);

            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>Project ${customProjId}</td>
                    <td>${e.expenseTitle}</td>
                    <td>${e.category}</td>
                    <td><strong>${formatCurrency(e.amount)}</strong></td>
                    <td>${e.expenseDate}</td>
                </tr>
            `;
        }).join('');

        let totalQuotations = 0;
        let quotationRows = quotations.map(q => {
            const customId = q.customId || ('QUO' + String(q.id).padStart(3, '0'));
            totalQuotations += (q.totalAmount || 0);

            return `
                <tr>
                    <td><strong>${customId}</strong></td>
                    <td>${q.projectName}</td>
                    <td>${q.clientName}</td>
                    <td>${formatCurrency(q.laborCost)}</td>
                    <td>${formatCurrency(q.materialCost)}</td>
                    <td>${formatCurrency(q.overheadCost)}</td>
                    <td><strong>${formatCurrency(q.totalAmount)}</strong></td>
                    <td><strong>${q.status}</strong></td>
                    <td>${q.generatedDate}</td>
                </tr>
            `;
        }).join('');

        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>ApexBuild - Financial Management Statement</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 30px; color: #1e293b; line-height: 1.5; }
                    .header { display: flex; justify-content: space-between; border-bottom: 2px solid #10b981; padding-bottom: 15px; margin-bottom: 25px; }
                    .title { font-size: 24px; font-weight: bold; color: #0f172a; }
                    .meta { font-size: 13px; color: #64748b; }
                    .summary-card { background: #d1fae5; border: 1px solid #a7f3d0; padding: 12px 18px; border-radius: 6px; margin-bottom: 20px; font-weight: 600; color: #065f46; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 25px; font-size: 13px; }
                    th, td { border: 1px solid #cbd5e1; padding: 9px 12px; text-align: left; }
                    th { background-color: #f1f5f9; color: #334155; font-weight: 600; }
                    h3 { color: #059669; margin-top: 25px; font-size: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; }
                    .footer { margin-top: 40px; font-size: 12px; color: #94a3b8; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 15px; }
                    @media print { .no-print { display: none; } }
                </style>
            </head>
            <body>
                <div class="no-print" style="margin-bottom: 15px;">
                    <button onclick="window.print()" style="padding: 8px 16px; background: #10b981; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Print / Save as PDF</button>
                    <button onclick="window.close()" style="padding: 8px 16px; background: #64748b; color: white; border: none; border-radius: 4px; cursor: pointer;">Close</button>
                </div>
                <div class="header">
                    <div>
                        <div class="title">🏗️ ApexBuild Construction ERP</div>
                        <div style="font-weight: 600; color: #059669;">Executive Financial Statement & Quotation Ledger</div>
                    </div>
                    <div class="meta" style="text-align: right;">
                        <div>Generated: ${now}</div>
                        <div>Generated By: ${generatorName}</div>
                        <div>Module: Site Accounting & Cost Engineering</div>
                    </div>
                </div>

                <div class="summary-card">
                    Total Disbursed Expenses: ${formatCurrency(totalExpenses)} &bull; Total Quotations Value: ${formatCurrency(totalQuotations)}
                </div>

                <h3>Project Expenditure & Site Vouchers (${expenses.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Voucher ID</th><th>Project Ref</th><th>Expense Description</th><th>Cost Category</th><th>Disbursed Amount</th><th>Date</th>
                        </tr>
                    </thead>
                    <tbody>${expenseRows}</tbody>
                </table>

                <h3>Client Cost Quotations & Project Estimates (${quotations.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Quote ID</th><th>Project Title</th><th>Client</th><th>Labor Cost</th><th>Material Cost</th><th>Overheads</th><th>Total Quotation</th><th>Status</th><th>Date</th>
                        </tr>
                    </thead>
                    <tbody>${quotationRows}</tbody>
                </table>

                <div class="footer">ApexBuild Enterprise Construction Management System &bull; Confidential Internal Report</div>
            </body>
            </html>
        `);
        printWindow.document.close();
    } catch (err) {
        showToast('Failed to generate financial report: ' + err.message, 'error');
    }
}
