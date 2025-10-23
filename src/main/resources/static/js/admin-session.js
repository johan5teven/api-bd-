// admin-session.js
// Ensures admin pages keep session state and handle logout centrally
async function ensureAdminSession() {
    try {
        const res = await fetch('/api/auth/session');
        if (res.status !== 200) {
            window.location.href = '/login.html';
            return;
        }
        const body = await res.json();
        // If the backend returns a role, require ADMIN
        if (!body.rol || !body.rol.toLowerCase().includes('admin')) {
            // Not admin: redirect to login
            window.location.href = '/login.html';
            return;
        }
        const infoEl = document.getElementById('sessionInfo');
        if (infoEl) infoEl.innerText = 'Conectado como: ' + (body.correo || body.id);
    } catch (e) {
        console.error('Error checking session', e);
        window.location.href = '/login.html';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            await fetch('/api/auth/logout', { method: 'POST' });
            window.location.href = '/login.html';
        });
    }
    ensureAdminSession();
});
