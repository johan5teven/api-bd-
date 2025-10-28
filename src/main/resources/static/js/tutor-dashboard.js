document.addEventListener('DOMContentLoaded', () => {
  const btn = document.getElementById('btnLoad');
  const out = document.getElementById('cursos');
  async function loadDashboardFor(tutorId) {
    if (!tutorId) { out.innerHTML = '<p>Id de tutor inválido.</p>'; return; }
    try {
      const res = await fetch('/api/tutor/dashboard?tutorId=' + encodeURIComponent(tutorId));
      if (!res.ok) { out.innerHTML = '<p>Error cargando dashboard: ' + res.status + '</p>'; return; }
      const cursos = await res.json();
      out.innerHTML = '';
      if (!cursos || cursos.length === 0) { out.innerHTML = '<p>No tiene cursos asignados.</p>'; return; }
      cursos.forEach(c => {
        const div = document.createElement('div'); div.className='card';
        const h = document.createElement('h3'); h.textContent = c.titulo; div.appendChild(h);
        const p = document.createElement('p'); p.textContent = c.descripcion; div.appendChild(p);
        const list = document.createElement('div');
        (c.inscripciones || []).forEach(i => {
          const s = document.createElement('div'); s.style.borderTop='1px solid #eee'; s.style.paddingTop='6px';
          s.innerHTML = `<strong>${i.estudiante_correo || i.estudiante_id}</strong> - Estado: ${i.estado || ''} - Progreso: ${i.progreso ?? 0}%`;
          list.appendChild(s);
        });
        div.appendChild(list);
        out.appendChild(div);
      });
    } catch(e){ console.error(e); out.innerHTML = '<p>Error cargando dashboard</p>'; }
  }

  btn.addEventListener('click', async () => {
    const id = document.getElementById('tutorId').value;
    if (!id) { alert('Ingrese id de tutor'); return; }
    await loadDashboardFor(id);
  });

  // Try to auto-fill tutorId from the logged-in session and auto-load the dashboard
  (async function tryAutoFillFromSession(){
    try {
      const s = await fetch('/api/auth/session');
      if (!s.ok) return; // not authenticated
      const sj = await s.json();
      // session object usually contains 'id' for the usuario; Tutor.id_tutor maps to Usuario.id
      const possibleId = sj && (sj.id || sj.userId || sj.id_usuario);
      if (possibleId) {
        const input = document.getElementById('tutorId');
        input.value = possibleId;
        // load immediately
        await loadDashboardFor(possibleId);
      }
    } catch(e){ /* silent */ }
  })();
});
