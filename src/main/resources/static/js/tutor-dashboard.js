document.addEventListener('DOMContentLoaded', () => {
  const btn = document.getElementById('btnLoad');
  const out = document.getElementById('cursos');
  btn.addEventListener('click', async () => {
    const id = document.getElementById('tutorId').value;
    if (!id) { alert('Ingrese id de tutor'); return; }
    try {
      const res = await fetch('/api/tutor/dashboard?tutorId=' + encodeURIComponent(id));
      const cursos = await res.json();
      out.innerHTML = '';
      if (!cursos.length) { out.innerHTML = '<p>No tiene cursos asignados.</p>'; return; }
      cursos.forEach(c => {
        const div = document.createElement('div'); div.className='card';
        const h = document.createElement('h3'); h.textContent = c.titulo; div.appendChild(h);
        const p = document.createElement('p'); p.textContent = c.descripcion; div.appendChild(p);
        const list = document.createElement('div');
        c.inscripciones.forEach(i => {
          const s = document.createElement('div'); s.style.borderTop='1px solid #eee'; s.style.paddingTop='6px';
          s.innerHTML = `<strong>${i.estudiante_correo || i.estudiante_id}</strong> - Estado: ${i.estado} - Progreso: ${i.progreso}%`;
          list.appendChild(s);
        });
        div.appendChild(list);
        out.appendChild(div);
      });
    } catch(e){ console.error(e); alert('Error cargando dashboard'); }
  });
});
