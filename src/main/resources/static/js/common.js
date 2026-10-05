// ====================================================================
// Web-based Construction Management System - Common Helper Functions
// ====================================================================

const API_BASE = '';

// Retrieve currently logged-in user from localStorage
function getCurrentUser() {
    const userJson = localStorage.getItem('currentUser');
    if (!userJson) return null;
    try {
        return JSON.parse(userJson);
    } catch (e) {
        return null;
    }
}

// Set session
function setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
}

// Log out user
function logout() {
    localStorage.removeItem('currentUser');
    window.location.href = 'index.html';
}

// Protect pages based on roles
function checkPageAuth(requiredPortal) {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = 'index.html';
        return null;
    }

    if (requiredPortal === 'CLIENT' && user.role !== 'CLIENT' && user.role !== 'ADMIN') {
        window.location.href = 'dashboard.html';
        return null;
    }

    if (requiredPortal === 'STAFF' && user.role === 'CLIENT') {
        window.location.href = 'client.html';
        return null;
    }

    return user;
}

// Show Toast Notification
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const bgClass = type === 'success' ? 'bg-success text-white' : (type === 'error' ? 'bg-danger text-white' : 'bg-warning text-dark');
    toast.className = `toast align-items-center ${bgClass} border-0 show mb-2 shadow`;
    toast.setAttribute('role', 'alert');
    toast.style.minWidth = '250px';

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body fw-medium">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.parentElement.parentElement.remove()"></button>
        </div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Format Currency in LKR
function formatCurrency(amount) {
    if (!amount) return 'Rs. 0.00';
    return 'Rs. ' + parseFloat(amount).toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
