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
      const ct = res.headers.get('content-type') || '';
      // If server returned HTML (likely a login page) show a helpful message
      if (!ct.includes('application/json')) {
        const text = await res.text();
        console.error('Non-JSON response from server creating tutor:', text.slice(0,500));
        alert('La respuesta del servidor no fue JSON. Es posible que no esté autenticado como admin. Por favor inicie sesión.');
        // redirect to login to make it easy for the admin
        window.location.href = '/login';
        return;
      }
      const j = await res.json();
      if (res.ok) { alert('Tutor creado id: ' + j.id_tutor); document.getElementById('btnListar').click(); }
      else alert('Error creando tutor: ' + (j.error || JSON.stringify(j)));
    } catch (e) { console.error(e); alert('Error creando tutor'); }
  });
});
