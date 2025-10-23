// estudiante-session.js
// Ensures estudiante pages keep session state and handle logout centrally
async function ensureEstudianteSession() {
    try {
        const res = await fetch('/api/auth/session');
        if (res.status !== 200) {
            window.location.href = '/login.html';
            return;
        }
        const body = await res.json();
        // Require estudiante role (backend returns 'rol')
        if (!body.rol || !body.rol.toLowerCase().includes('estudiante')) {
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
    ensureEstudianteSession();
});
