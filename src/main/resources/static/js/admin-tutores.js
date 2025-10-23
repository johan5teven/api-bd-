document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('#tabla tbody');
  const btnCrear = document.getElementById('btnCrear');
  const inpCorreo = document.getElementById('inpCorreo');
  const inpEspecialidad = document.getElementById('inpEspecialidad');
  document.getElementById('btnListar').addEventListener('click', async () => {
    tbody.innerHTML = '';
    try {
      const res = await fetch('/api/admin/tutores/listar');
      const datos = await res.json();
      datos.forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${t.id_tutor}</td><td>${t.correo ?? t.nombre ?? ''}</td><td>${t.especialidad ?? ''}</td><td>${t.activo ?? ''}</td>`;
        tbody.appendChild(tr);
      });
    } catch (e) { console.error(e); }
  });

  btnCrear.addEventListener('click', async () => {
    const correo = inpCorreo.value.trim();
    const especialidad = inpEspecialidad.value.trim();
    if (!correo) { alert('Ingrese correo'); return; }
    try {
      const res = await fetch('/api/admin/tutores/crear', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ correo, especialidad }) });
      const j = await res.json();
      if (res.ok) { alert('Tutor creado id: ' + j.id_tutor); document.getElementById('btnListar').click(); }
      else alert('Error: ' + (j.error || JSON.stringify(j)));
    } catch (e) { console.error(e); alert('Error creando tutor'); }
  });
});
