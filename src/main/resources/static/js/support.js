// ====================================================================
// Member 2: Weerawansha K.H.H. (IT25103631)
// Module: System Security, Support (Inquiries) & Site Progress (CRUD)
// ====================================================================

// ==========================================
// 1. INQUIRIES & COMPLAINTS CRUD
// ==========================================

async function loadInquiries() {
    const tbody = document.getElementById('inquiries-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/support/inquiries');
        const list = await res.json();
        if (!res.ok || !Array.isArray(list)) {
            throw new Error(list?.message || list?.error || 'Failed to load inquiries');
        }

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No client inquiries submitted.</td></tr>';
            return;
        }

        list.forEach(item => {
            const tr = document.createElement('tr');
            let statusBadge = '<span class="badge bg-warning text-dark">OPEN</span>';
            if (item.status === 'INVESTIGATING') statusBadge = '<span class="badge bg-info text-dark">INVESTIGATING</span>';
            if (item.status === 'RESOLVED') statusBadge = '<span class="badge bg-success">RESOLVED</span>';

            tr.innerHTML = `
                <td><strong>#${item.id}</strong></td>
                <td><strong>${item.clientName}</strong><br><small class="text-muted">${item.clientEmail}</small></td>
                <td>
                    ${item.projectId ? `<span class="badge bg-secondary mb-1">Project #${item.projectId}</span><br>` : ''}
                    <strong>${item.subject}</strong>
                </td>
                <td><span class="text-truncate d-inline-block" style="max-width: 250px;">${item.message}</span></td>
                <td>${statusBadge}</td>
                <td>${item.resolution || '<span class="text-muted fst-italic">Pending reply</span>'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openResolveInquiryModal(${item.id})">Reply</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteInquiry(${item.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load inquiries: ' + err.message, 'error');
    }
}

async function openResolveInquiryModal(id) {
    try {
        const res = await fetch(`/api/support/inquiries/${id}`);
        const item = await res.json();

        document.getElementById('inquiry-id').value = item.id;
        document.getElementById('inquiry-client-info').innerText = `${item.clientName} (${item.clientEmail})`;
        const projInfo = document.getElementById('inquiry-project-info');
        if (projInfo) {
            projInfo.innerHTML = item.projectId 
                ? `Related Project: <span class="badge bg-secondary">Project #${item.projectId}</span>` 
                : `Related Project: <span class="text-muted fst-italic">General (No Project)</span>`;
        }
        document.getElementById('inquiry-subject-display').innerText = item.subject;
        document.getElementById('inquiry-message-display').innerText = item.message;
        document.getElementById('inquiry-status').value = item.status;
        document.getElementById('inquiry-resolution').value = item.resolution || '';

        new bootstrap.Modal(document.getElementById('inquiryModal')).show();
    } catch (err) {
        showToast('Failed to load inquiry: ' + err.message, 'error');
    }
}

async function saveInquiryReply(event) {
    event.preventDefault();
    const id = document.getElementById('inquiry-id').value;
    try {
        const res = await fetch(`/api/support/inquiries/${id}`);
        const current = await res.json();

        current.status = document.getElementById('inquiry-status').value;
        current.resolution = document.getElementById('inquiry-resolution').value;

        if (current.status === 'RESOLVED' && (!current.resolution || !current.resolution.trim())) {
            showToast('Resolution details are required when marking ticket as RESOLVED.', 'error');
            return;
        }

        const updateRes = await fetch(`/api/support/inquiries/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(current)
        });

        if (!updateRes.ok) {
            const err = await updateRes.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to update inquiry');
        }

        showToast('Inquiry response updated successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('inquiryModal'));
        if (modal) modal.hide();
        loadInquiries();
    } catch (err) {
        showToast('Validation Error: ' + err.message, 'error');
    }
}

async function deleteInquiry(id) {
    if (!confirm('Are you sure you want to delete this inquiry ticket?')) return;
    try {
        const res = await fetch(`/api/support/inquiries/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());
        showToast('Inquiry deleted successfully!');
        loadInquiries();
    } catch (err) {
        showToast('Failed to delete inquiry: ' + err.message, 'error');
    }
}

// ==========================================
// 2. DAILY SITE PROGRESS LOGS CRUD
// ==========================================

async function loadProgressLogs() {
    const tbody = document.getElementById('progress-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/support/progress');
        const logs = await res.json();
        if (!res.ok || !Array.isArray(logs)) {
            throw new Error(logs?.message || logs?.error || 'Failed to load progress logs');
        }

        tbody.innerHTML = '';
        if (logs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No site progress logs recorded yet.</td></tr>';
            return;
        }

        logs.forEach(l => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>#${l.id}</strong></td>
                <td>Project #${l.projectId}</td>
                <td>${l.logDate}</td>
                <td><strong>${l.milestone}</strong></td>
                <td>${l.workDone}</td>
                <td>${l.reportedBy}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditProgressModal(${l.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteProgressLog(${l.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load progress logs: ' + err.message, 'error');
    }
}

function openNewProgressModal() {
    document.getElementById('progress-id').value = '';
    document.getElementById('progress-form').reset();
    document.getElementById('progress-date').value = new Date().toISOString().split('T')[0];
    const user = getCurrentUser();
    if (user) document.getElementById('progress-reporter').value = user.fullName;
    new bootstrap.Modal(document.getElementById('progressModal')).show();
}

async function openEditProgressModal(id) {
    try {
        const res = await fetch('/api/support/progress');
        const logs = await res.json();
        const l = logs.find(item => item.id === id);
        if (!l) throw new Error('Log not found');

        document.getElementById('progress-id').value = l.id;
        document.getElementById('progress-project-id').value = l.projectId;
        document.getElementById('progress-reporter').value = l.reportedBy;
        document.getElementById('progress-date').value = l.logDate;
        document.getElementById('progress-milestone').value = l.milestone;
        document.getElementById('progress-work-done').value = l.workDone;
        document.getElementById('progress-issues').value = l.issuesFaced || '';

        new bootstrap.Modal(document.getElementById('progressModal')).show();
    } catch (err) {
        showToast('Failed to load progress log: ' + err.message, 'error');
    }
}

async function saveProgressLog(event) {
    event.preventDefault();
    const id = document.getElementById('progress-id').value;
    const data = {
        projectId: document.getElementById('progress-project-id').value,
        reportedBy: document.getElementById('progress-reporter').value,
        logDate: document.getElementById('progress-date').value,
        milestone: document.getElementById('progress-milestone').value,
        workDone: document.getElementById('progress-work-done').value,
        issuesFaced: document.getElementById('progress-issues').value
    };

    // Client-side validations
    const today = new Date().toISOString().split('T')[0];
    if (data.logDate > today) {
        showToast('Progress log date cannot be in the future.', 'error');
        return;
    }
    if (!data.milestone || !data.milestone.trim()) {
        showToast('Milestone title cannot be empty.', 'error');
        return;
    }
    if (!data.workDone || !data.workDone.trim()) {
        showToast('Work performed details cannot be empty.', 'error');
        return;
    }

    try {
        let res;
        if (id) {
            res = await fetch(`/api/support/progress/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/api/support/progress', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || err.error || 'Failed to save progress report');
        }

        showToast(id ? 'Progress log updated!' : 'Progress report recorded!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('progressModal'));
        if (modal) modal.hide();
        loadProgressLogs();
    } catch (err) {
        showToast('Validation Error: ' + err.message, 'error');
    }
}

async function deleteProgressLog(id) {
    if (!confirm('Are you sure you want to delete this progress report?')) return;
    try {
        const res = await fetch(`/api/support/progress/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());
        showToast('Progress report deleted successfully!');
        loadProgressLogs();
    } catch (err) {
        showToast('Failed to delete progress report: ' + err.message, 'error');
    }
}

// ==========================================
// 3. USER & STAFF ACCOUNTS MANAGEMENT CRUD
// ==========================================

async function loadStaffUsers() {
    const tbody = document.getElementById('users-table-body');
    if (!tbody) return;

    const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
    const isAdmin = currentUser && currentUser.role === 'ADMIN';

    // Show or hide Add Staff button based on Admin role
    const addStaffBtn = document.getElementById('btn-add-staff-member');
    if (addStaffBtn) {
        if (isAdmin) {
            addStaffBtn.classList.remove('d-none');
        } else {
            addStaffBtn.classList.add('d-none');
        }
    }

    try {
        const res = await fetch('/api/auth/users');
        const users = await res.json();
        if (!res.ok || !Array.isArray(users)) {
            throw new Error(users?.message || users?.error || 'Failed to load users');
        }

        tbody.innerHTML = '';
        users.forEach(u => {
            const tr = document.createElement('tr');
            let roleBadge = `<span class="badge bg-secondary">${u.role}</span>`;
            if (u.role === 'ADMIN') roleBadge = `<span class="badge bg-danger">ADMIN</span>`;
            if (u.role === 'PROJECT_MANAGER') roleBadge = `<span class="badge bg-primary">PROJECT MANAGER</span>`;
            if (u.role === 'SITE_ENGINEER') roleBadge = `<span class="badge bg-info text-dark">SITE ENGINEER</span>`;
            if (u.role === 'STOREKEEPER') roleBadge = `<span class="badge bg-warning text-dark">STOREKEEPER</span>`;
            if (u.role === 'FINANCE_OFFICER') roleBadge = `<span class="badge bg-success">FINANCE</span>`;
            if (u.role === 'PROCUREMENT_OFFICER') roleBadge = `<span class="badge bg-dark">PROCUREMENT</span>`;
            if (u.role === 'CLIENT') roleBadge = `<span class="badge bg-light text-dark border">CLIENT</span>`;

            let actionHtml = '';
            if (isAdmin) {
                actionHtml = `
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditUserModal(${u.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${u.id})">Delete</button>
                `;
            } else {
                actionHtml = `<span class="badge bg-secondary">Admin Only</span>`;
            }

            tr.innerHTML = `
                <td><strong>#${u.id}</strong></td>
                <td><strong>${u.fullName}</strong></td>
                <td>${u.email}</td>
                <td>${roleBadge}</td>
                <td>${u.phone || 'N/A'}</td>
                <td>${actionHtml}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load users: ' + err.message, 'error');
    }
}

function openNewStaffModal() {
    const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
    if (!currentUser || currentUser.role !== 'ADMIN') {
        showToast('Access Denied: Only Admin can add new staff members.', 'error');
        return;
    }

    document.getElementById('user-id').value = '';
    document.getElementById('user-form').reset();
    document.getElementById('user-email').readOnly = false;
    document.getElementById('userModalTitle').innerText = 'Add New Staff Member';
    new bootstrap.Modal(document.getElementById('userModal')).show();
}

async function openEditUserModal(id) {
    const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
    if (!currentUser || currentUser.role !== 'ADMIN') {
        showToast('Access Denied: Only Admin can edit staff accounts.', 'error');
        return;
    }

    try {
        const res = await fetch('/api/auth/users');
        const users = await res.json();
        const u = users.find(x => x.id === id);
        if (!u) throw new Error('User not found');

        document.getElementById('user-id').value = u.id;
        document.getElementById('user-fullname').value = u.fullName;
        document.getElementById('user-email').value = u.email;
        document.getElementById('user-email').readOnly = true;
        document.getElementById('user-role').value = u.role;
        document.getElementById('user-phone').value = u.phone || '';
        document.getElementById('user-address').value = u.address || '';
        document.getElementById('user-password').placeholder = 'Leave blank to keep current';

        document.getElementById('userModalTitle').innerText = 'Edit User / Staff Account';
        new bootstrap.Modal(document.getElementById('userModal')).show();
    } catch (err) {
        showToast('Failed to load user: ' + err.message, 'error');
    }
}

async function saveUserAccount(event) {
    event.preventDefault();
    const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
    if (!currentUser || currentUser.role !== 'ADMIN') {
        showToast('Access Denied: Only Admin can save staff accounts.', 'error');
        return;
    }

    const id = document.getElementById('user-id').value;
    const userData = {
        fullName: document.getElementById('user-fullname').value,
        email: document.getElementById('user-email').value,
        role: document.getElementById('user-role').value,
        phone: document.getElementById('user-phone').value,
        address: document.getElementById('user-address').value
    };

    const pass = document.getElementById('user-password').value;
    if (pass) userData.password = pass;

    try {
        let res;
        if (id) {
            res = await fetch(`/api/auth/users/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData)
            });
        } else {
            if (!pass) throw new Error('Password is required for new accounts');
            userData.password = pass;
            res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData)
            });
        }

        if (!res.ok) throw new Error(await res.text());

        showToast(id ? 'Account updated!' : 'New staff member added successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('userModal'));
        if (modal) modal.hide();
        loadStaffUsers();
    } catch (err) {
        showToast('Error saving user: ' + err.message, 'error');
    }
}

async function deleteUser(id) {
    const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
    if (!currentUser || currentUser.role !== 'ADMIN') {
        showToast('Access Denied: Only Admin can delete staff accounts.', 'error');
        return;
    }

    if (!confirm('Are you sure you want to remove this user account?')) return;
    try {
        const res = await fetch(`/api/auth/users/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());
        showToast('User account removed!');
        loadStaffUsers();
    } catch (err) {
        showToast('Failed to delete user: ' + err.message, 'error');
    }
}

// ==========================================
// 4. SITE QA & PROGRESS REPORT GENERATOR
// ==========================================

async function printSupportReport() {
    try {
        const [progressRes, inquiriesRes] = await Promise.all([
            fetch('/api/support/progress'),
            fetch('/api/support/inquiries')
        ]);

        const progressLogs = await progressRes.json();
        const inquiries = await inquiriesRes.json();

        const printWindow = window.open('', '_blank');
        if (!printWindow) {
            showToast('Pop-up blocked. Please allow pop-ups to print the report.', 'error');
            return;
        }

        const now = new Date().toLocaleString();
        const currentUser = (typeof getCurrentUser === 'function') ? getCurrentUser() : null;
        const generatorName = currentUser ? `${currentUser.fullName} (${currentUser.role})` : 'Weerawansha K.H.H. (Site Engineer)';

        let progressRows = progressLogs.map(p => `
            <tr>
                <td>#${p.id}</td>
                <td>Project #${p.projectId}</td>
                <td>${p.logDate}</td>
                <td><strong>${p.milestone}</strong></td>
                <td>${p.workDone}</td>
                <td>${p.reportedBy}</td>
                <td>${p.issuesFaced ? `<span style="color: #dc2626;">${p.issuesFaced}</span>` : '<span style="color: #16a34a;">None</span>'}</td>
            </tr>
        `).join('');

        let inquiriesRows = inquiries.map(q => `
            <tr>
                <td>#${q.id}</td>
                <td>${q.clientName} (${q.clientEmail})</td>
                <td>${q.projectId ? `Project #${q.projectId}` : 'General'}</td>
                <td><strong>${q.subject}</strong></td>
                <td>${q.message}</td>
                <td><strong>${q.status}</strong></td>
                <td>${q.resolution || 'Pending reply'}</td>
            </tr>
        `).join('');

        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>ApexBuild - Site Engineering & QA Progress Report</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 30px; color: #1e293b; line-height: 1.5; }
                    .header { display: flex; justify-content: space-between; border-bottom: 2px solid #0284c7; padding-bottom: 15px; margin-bottom: 25px; }
                    .title { font-size: 24px; font-weight: bold; color: #0f172a; }
                    .meta { font-size: 13px; color: #64748b; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 25px; font-size: 13px; }
                    th, td { border: 1px solid #cbd5e1; padding: 9px 12px; text-align: left; }
                    th { background-color: #f1f5f9; color: #334155; font-weight: 600; }
                    h3 { color: #0284c7; margin-top: 25px; font-size: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; }
                    .footer { margin-top: 40px; font-size: 12px; color: #94a3b8; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 15px; }
                    @media print { .no-print { display: none; } }
                </style>
            </head>
            <body>
                <div class="no-print" style="margin-bottom: 15px;">
                    <button onclick="window.print()" style="padding: 8px 16px; background: #0284c7; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Print / Save as PDF</button>
                    <button onclick="window.close()" style="padding: 8px 16px; background: #64748b; color: white; border: none; border-radius: 4px; cursor: pointer;">Close</button>
                </div>
                <div class="header">
                    <div>
                        <div class="title">🏗️ ApexBuild Construction ERP</div>
                        <div style="font-weight: 600; color: #0284c7;">Site Progress & Quality Assurance (QA) Report</div>
                    </div>
                    <div class="meta" style="text-align: right;">
                        <div>Generated: ${now}</div>
                        <div>Generated By: ${generatorName}</div>
                        <div>Module: Site Engineering & Security Oversight</div>
                    </div>
                </div>

                <h3>Daily Site Progress & Milestone Logs (${progressLogs.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Log ID</th><th>Project Ref</th><th>Date</th><th>Milestone Achieved</th><th>Work Completed</th><th>Reported By</th><th>Issues / Bottlenecks</th>
                        </tr>
                    </thead>
                    <tbody>${progressRows}</tbody>
                </table>

                <h3>Client Quality Support & Field Inquiries (${inquiries.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Ticket ID</th><th>Client Contact</th><th>Project</th><th>Subject</th><th>Inquiry Details</th><th>Status</th><th>Resolution Notes</th>
                        </tr>
                    </thead>
                    <tbody>${inquiriesRows}</tbody>
                </table>

                <div class="footer">ApexBuild Enterprise Construction Management System &bull; Confidential Internal Report</div>
            </body>
            </html>
        `);
        printWindow.document.close();
    } catch (err) {
        showToast('Failed to generate QA report: ' + err.message, 'error');
    }
}

