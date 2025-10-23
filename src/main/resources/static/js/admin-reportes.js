document.addEventListener('DOMContentLoaded', async () => {
  const u = document.getElementById('usuarios');
  const v = document.getElementById('ventas');
  const a = document.getElementById('actividad');
  try {
    const ru = await fetch('/api/admin/reportes/usuarios/serie').then(r=>r.json());
    u.textContent = JSON.stringify(ru, null, 2);
    const rv = await fetch('/api/admin/reportes/ventas/serie').then(r=>r.json());
    v.textContent = JSON.stringify(rv, null, 2);
    const ra = await fetch('/api/admin/reportes/actividad/reciente').then(r=>r.json());
    a.innerHTML = '';
    ra.forEach(it => { const li = document.createElement('li'); li.textContent = `[${it.fecha}] ${it.mensaje}`; a.appendChild(li); });
  } catch (e) { console.error(e); }
});
