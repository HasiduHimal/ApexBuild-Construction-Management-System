// ====================================================================
// Member 1: Wijesekera S.D.R. (IT25102552)
// Module: Project Management & Construction Tasks (CRUD)
// ====================================================================

// Load all projects into the projects table
async function loadProjects() {
    const tbody = document.getElementById('projects-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/projects');
        const projects = await res.json();
        if (!res.ok || !Array.isArray(projects)) {
            throw new Error(projects?.message || projects?.error || 'Failed to load projects');
        }

        tbody.innerHTML = '';
        if (projects.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No projects found. Click "+ Add New Project" to create one.</td></tr>';
            return;
        }

        projects.forEach(p => {
            const tr = document.createElement('tr');
            let statusBadge = `<span class="badge badge-planned">${p.status}</span>`;
            if (p.status === 'IN_PROGRESS') statusBadge = `<span class="badge badge-in-progress">IN PROGRESS</span>`;
            if (p.status === 'COMPLETED') statusBadge = `<span class="badge badge-completed">COMPLETED</span>`;
            if (p.status === 'ON_HOLD') statusBadge = `<span class="badge badge-on-hold">ON HOLD</span>`;

            const planLink = p.housePlanFile
                ? `<a href="${p.housePlanFile}" target="_blank" class="btn btn-sm btn-outline-primary text-nowrap"><i class="bi bi-file-earmark-pdf me-1"></i>View Plan</a>`
                : '<span class="text-light text-opacity-50 small">None</span>';

            const formattedPid = p.customId || ('PID' + String(p.id).padStart(3, '0'));

            tr.innerHTML = `
                <td><strong class="text-info">${formattedPid}</strong></td>
                <td><strong>${p.projectName}</strong></td>
                <td>${p.location}</td>
                <td>${p.clientName || 'N/A'}</td>
                <td>${formatCurrency(p.estimatedBudget)}</td>
                <td>${statusBadge}</td>
                <td>${planLink}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditProjectModal(${p.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteProject(${p.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Update Project select dropdowns in task modals
        updateProjectDropdowns(projects);
    } catch (err) {
        showToast('Failed to load projects: ' + err.message, 'error');
    }
}

// Populate project dropdowns
function updateProjectDropdowns(projects) {
    const selects = document.querySelectorAll('.project-selector');
    selects.forEach(sel => {
        const currentVal = sel.value;
        sel.innerHTML = '<option value="">-- Select Project --</option>';
        projects.forEach(p => {
            const formattedPid = p.customId || ('PID' + String(p.id).padStart(3, '0'));
            sel.innerHTML += `<option value="${p.id}">${formattedPid} - ${p.projectName}</option>`;
        });
        if (currentVal) sel.value = currentVal;
    });
}

// CREATE / UPDATE Project
async function saveProject(event) {
    event.preventDefault();
    const id = document.getElementById('project-id').value;
    const projectData = {
        projectName: document.getElementById('project-name').value,
        description: document.getElementById('project-description').value,
        location: document.getElementById('project-location').value,
        clientName: document.getElementById('project-client-name').value,
        startDate: document.getElementById('project-start-date').value || null,
        endDate: document.getElementById('project-end-date').value || null,
        estimatedBudget: parseFloat(document.getElementById('project-budget').value) || 0,
        status: document.getElementById('project-status').value
    };

    // Client-side validations
    if (projectData.estimatedBudget < 0) {
        showToast('Estimated budget cannot be negative. Value must be greater than or equal to 0.', 'error');
        return;
    }

    if (projectData.startDate && projectData.endDate && projectData.endDate < projectData.startDate) {
        showToast('Project end date cannot be earlier than start date.', 'error');
        return;
    }

    try {
        let res;
        if (id) {
            // UPDATE
            res = await fetch(`/api/projects/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(projectData)
            });
        } else {
            // CREATE
            res = await fetch('/api/projects', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(projectData)
            });
        }

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            throw new Error(errData.message || errData.error || 'Failed to save project');
        }

        showToast(id ? 'Project updated successfully!' : 'Project created successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('projectModal'));
        if (modal) modal.hide();
        loadProjects();
    } catch (err) {
        showToast('Validation Error: ' + err.message, 'error');
    }
}

// Open Edit Modal
async function openEditProjectModal(id) {
    try {
        const res = await fetch(`/api/projects/${id}`);
        const p = await res.json();

        document.getElementById('project-id').value = p.id;
        document.getElementById('project-name').value = p.projectName;
        document.getElementById('project-description').value = p.description || '';
        document.getElementById('project-location').value = p.location;
        document.getElementById('project-client-name').value = p.clientName || '';
        document.getElementById('project-start-date').value = p.startDate || '';
        document.getElementById('project-end-date').value = p.endDate || '';
        document.getElementById('project-budget').value = p.estimatedBudget;
        document.getElementById('project-status').value = p.status;

        const planPreviewContainer = document.getElementById('project-plan-preview-container');
        const planLinkElem = document.getElementById('project-plan-link');
        if (planPreviewContainer && planLinkElem) {
            if (p.housePlanFile) {
                planPreviewContainer.style.display = 'block';
                planLinkElem.innerHTML = `<a href="${p.housePlanFile}" target="_blank" class="btn btn-sm btn-outline-primary"><i class="bi bi-file-earmark-pdf me-1"></i>Open Attached Blueprint</a>`;
            } else {
                planPreviewContainer.style.display = 'none';
                planLinkElem.innerHTML = '';
            }
        }

        document.getElementById('projectModalTitle').innerText = 'Edit Construction Project';
        new bootstrap.Modal(document.getElementById('projectModal')).show();
    } catch (err) {
        showToast('Failed to fetch project details: ' + err.message, 'error');
    }
}

// Reset Project Modal for Create
function openNewProjectModal() {
    document.getElementById('project-id').value = '';
    document.getElementById('project-form').reset();
    const planPreviewContainer = document.getElementById('project-plan-preview-container');
    if (planPreviewContainer) planPreviewContainer.style.display = 'none';
    document.getElementById('projectModalTitle').innerText = 'Create New Construction Project';
    new bootstrap.Modal(document.getElementById('projectModal')).show();
}

// DELETE Project
async function deleteProject(id) {
    if (!confirm('Are you sure you want to delete this project and all its associated records?')) return;

    try {
        const res = await fetch(`/api/projects/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());

        showToast('Project deleted successfully!');
        loadProjects();
    } catch (err) {
        showToast('Failed to delete project: ' + err.message, 'error');
    }
}

// ==========================================
// TASKS MANAGEMENT (CRUD)
// ==========================================

async function loadTasks() {
    const tbody = document.getElementById('tasks-table-body');
    if (!tbody) return;

    try {
        const res = await fetch('/api/projects/tasks');
        const tasks = await res.json();
        if (!res.ok || !Array.isArray(tasks)) {
            throw new Error(tasks?.message || tasks?.error || 'Failed to load tasks');
        }

        tbody.innerHTML = '';
        if (tasks.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No tasks assigned yet.</td></tr>';
            return;
        }

        tasks.forEach(t => {
            const tr = document.createElement('tr');
            let priorityBadge = '<span class="badge bg-secondary text-white">LOW</span>';
            if (t.priority === 'HIGH') priorityBadge = '<span class="badge bg-warning text-dark fw-bold"><i class="bi bi-arrow-up-circle me-1"></i>HIGH</span>';
            if (t.priority === 'URGENT') priorityBadge = '<span class="badge bg-danger text-white fw-bold"><i class="bi bi-exclamation-triangle-fill me-1"></i>URGENT</span>';
            if (t.priority === 'MEDIUM') priorityBadge = '<span class="badge bg-info text-dark fw-bold"><i class="bi bi-dash-circle me-1"></i>MEDIUM</span>';

            let statusClass = 'bg-secondary';
            if (t.status === 'IN_PROGRESS') statusClass = 'bg-info text-dark';
            if (t.status === 'COMPLETED') statusClass = 'bg-success';

            tr.innerHTML = `
                <td><strong>#${t.id}</strong></td>
                <td>Project #${t.projectId}</td>
                <td><strong>${t.taskName}</strong></td>
                <td>${t.assignedWorker || 'Unassigned'}</td>
                <td>${priorityBadge}</td>
                <td><span class="badge ${statusClass}">${t.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditTaskModal(${t.id})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteTask(${t.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast('Failed to load tasks: ' + err.message, 'error');
    }
}

async function saveTask(event) {
    event.preventDefault();
    const id = document.getElementById('task-id').value;
    const taskData = {
        projectId: document.getElementById('task-project-id').value,
        taskName: document.getElementById('task-name').value,
        description: document.getElementById('task-description').value,
        assignedWorker: document.getElementById('task-worker').value,
        priority: document.getElementById('task-priority').value,
        status: document.getElementById('task-status').value,
        dueDate: document.getElementById('task-due-date').value || null
    };

    try {
        let res;
        if (id) {
            res = await fetch(`/api/projects/tasks/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(taskData)
            });
        } else {
            res = await fetch('/api/projects/tasks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(taskData)
            });
        }

        if (!res.ok) throw new Error(await res.text());

        showToast(id ? 'Task updated!' : 'Task created and assigned successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('taskModal'));
        if (modal) modal.hide();
        loadTasks();
    } catch (err) {
        showToast('Error saving task: ' + err.message, 'error');
    }
}

async function openEditTaskModal(id) {
    try {
        const res = await fetch(`/api/projects/tasks/${id}`);
        const t = await res.json();

        document.getElementById('task-id').value = t.id;
        document.getElementById('task-project-id').value = t.projectId;
        document.getElementById('task-name').value = t.taskName;
        document.getElementById('task-description').value = t.description || '';
        document.getElementById('task-worker').value = t.assignedWorker || '';
        document.getElementById('task-priority').value = t.priority;
        document.getElementById('task-status').value = t.status;
        document.getElementById('task-due-date').value = t.dueDate || '';

        document.getElementById('taskModalTitle').innerText = 'Edit Construction Task';
        new bootstrap.Modal(document.getElementById('taskModal')).show();
    } catch (err) {
        showToast('Failed to fetch task: ' + err.message, 'error');
    }
}

function openNewTaskModal() {
    document.getElementById('task-id').value = '';
    document.getElementById('task-form').reset();
    document.getElementById('taskModalTitle').innerText = 'Assign New Task';
    new bootstrap.Modal(document.getElementById('taskModal')).show();
}

async function deleteTask(id) {
    if (!confirm('Are you sure you want to remove this task?')) return;
    try {
        const res = await fetch(`/api/projects/tasks/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());
        showToast('Task removed successfully!');
        loadTasks();
    } catch (err) {
        showToast('Failed to delete task: ' + err.message, 'error');
    }
}

// PROJECT MANAGER REPORT GENERATOR (Page 3 of update requirements)
async function printProjectReport() {
    showToast('Generating Project Summary Report... Preparing print view.', 'info');
    try {
        const [projRes, taskRes] = await Promise.all([
            fetch('/api/projects'),
            fetch('/api/projects/tasks')
        ]);
        const projects = await projRes.json();
        const tasks = await taskRes.json();

        const printWindow = window.open('', '_blank');
        const now = new Date().toLocaleString();

        let projectsRows = projects.map(p => `
            <tr>
                <td><strong>${p.customId || ('PID' + String(p.id).padStart(3, '0'))}</strong></td>
                <td><strong>${p.projectName}</strong></td>
                <td>${p.location}</td>
                <td>${p.clientName || 'N/A'}</td>
                <td>${p.startDate || '-'} to ${p.endDate || '-'}</td>
                <td>${formatCurrency(p.estimatedBudget)}</td>
                <td><span style="font-weight: bold;">${p.status}</span></td>
            </tr>
        `).join('');

        let tasksRows = tasks.map(t => `
            <tr>
                <td>#${t.id}</td>
                <td>Project #${t.projectId}</td>
                <td>${t.taskName}</td>
                <td>${t.assignedWorker || 'Unassigned'}</td>
                <td><strong>${t.priority}</strong></td>
                <td>${t.status}</td>
                <td>${t.dueDate || '-'}</td>
            </tr>
        `).join('');

        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>ApexBuild - Project Management Executive Report</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 30px; color: #1e293b; line-height: 1.5; }
                    .header { display: flex; justify-content: space-between; border-bottom: 2px solid #0ea5e9; padding-bottom: 15px; margin-bottom: 25px; }
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
                    <button onclick="window.print()" style="padding: 8px 16px; background: #0ea5e9; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Print / Save as PDF</button>
                    <button onclick="window.close()" style="padding: 8px 16px; background: #64748b; color: white; border: none; border-radius: 4px; cursor: pointer;">Close</button>
                </div>
                <div class="header">
                    <div>
                        <div class="title">🏗️ ApexBuild Construction ERP</div>
                        <div style="font-weight: 600; color: #0284c7;">Project & Construction Tasks Management Report</div>
                    </div>
                    <div class="meta" style="text-align: right;">
                        <div>Generated: ${now}</div>
                        <div>Generated By: S.D.R. Wijesekera (Project Manager)</div>
                        <div>Scope: Civil Operations & Scheduling</div>
                    </div>
                </div>

                <h3>Active & Planned Construction Projects (${projects.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Project ID</th><th>Project Title</th><th>Location</th><th>Client</th><th>Duration</th><th>Budget (LKR)</th><th>Status</th>
                        </tr>
                    </thead>
                    <tbody>${projectsRows}</tbody>
                </table>

                <h3>Workforce Construction Tasks (${tasks.length})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Task ID</th><th>Project Ref</th><th>Task Title</th><th>Assigned Crew</th><th>Priority</th><th>Status</th><th>Due Date</th>
                        </tr>
                    </thead>
                    <tbody>${tasksRows}</tbody>
                </table>

                <div class="footer">ApexBuild Enterprise Construction Management System &bull; Confidential Internal Report</div>
            </body>
            </html>
        `);
        printWindow.document.close();
    } catch (err) {
        showToast('Failed to generate project report: ' + err.message, 'error');
    }
}
