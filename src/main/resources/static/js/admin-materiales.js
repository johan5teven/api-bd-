document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('#tabla tbody');
  document.getElementById('btnListar').addEventListener('click', async () => {
    tbody.innerHTML = '';
    const res = await fetch('/api/admin/materiales/listar');
    const datos = await res.json();
    datos.forEach(m => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${m.id_material}</td><td>${m.titulo ?? ''}</td><td>${m.tipo ?? ''}</td><td>${m.curso_id ?? ''}</td>`;
      tbody.appendChild(tr);
    });
  });

  document.getElementById('btnCrear').addEventListener('click', async () => {
    const titulo = document.getElementById('matTitulo').value;
    const tipo = document.getElementById('matTipo').value;
    const curso_id = parseInt(document.getElementById('matCurso').value || '0');
    const body = { titulo, tipo, curso_id: curso_id || undefined };
    const res = await fetch('/api/admin/materiales/crear', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
    const data = await res.json();
    if (res.ok) alert('Creado: ' + JSON.stringify(data)); else alert('Error: ' + JSON.stringify(data));
  });
});
