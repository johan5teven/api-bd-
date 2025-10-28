document.addEventListener('DOMContentLoaded', () => {
  const tablaBody = document.querySelector('#tablaPagos tbody');
  const mensajes = document.getElementById('mensajes');

  function mostrarMensaje(msg, err) {
    const li = document.createElement('li');
    li.textContent = msg;
    li.style.color = err ? 'red' : 'green';
    mensajes.prepend(li);
    setTimeout(() => li.remove(), 8000);
  }

  async function listarTodos() {
    tablaBody.innerHTML = '';
    try {
      const res = await fetch('/api/admin/pagos/listar');
      const datos = await res.json();
      if (!res.ok) { mostrarMensaje('Error listando: ' + JSON.stringify(datos), true); return; }
      datos.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.id_pago ?? ''}</td><td>${p.id_orden ?? ''}</td><td>${p.id_usuario ?? ''}</td><td>${p.metodo ?? ''}</td><td>${p.fecha_pago ?? ''}</td><td>${p.monto ?? ''}</td><td>${p.estado ?? ''}</td>`;
        tablaBody.appendChild(tr);
      });
    } catch (e) { mostrarMensaje(e.message, true); }
  }

  async function buscarPorUsuario(id) {
    tablaBody.innerHTML = '';
    try {
      const res = await fetch('/api/admin/pagos/usuario/' + id);
      const datos = await res.json();
      if (!res.ok) { mostrarMensaje('Error: ' + JSON.stringify(datos), true); return; }
      datos.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.id_pago ?? ''}</td><td>${p.id_orden ?? ''}</td><td>${p.id_usuario ?? ''}</td><td>${p.metodo ?? ''}</td><td>${p.fecha_pago ?? ''}</td><td>${p.monto ?? ''}</td><td>${p.estado ?? ''}</td>`;
        tablaBody.appendChild(tr);
      });
    } catch (e) { mostrarMensaje(e.message, true); }
  }

  document.getElementById('btnListar').addEventListener('click', listarTodos);
  document.getElementById('btnBuscar').addEventListener('click', () => {
    const id = document.getElementById('usuarioId').value;
    if (!id) return mostrarMensaje('Ingrese un id de usuario', true);
    buscarPorUsuario(id);
  });

  document.getElementById('btnRegistrar').addEventListener('click', async () => {
    const idOrden = parseInt(document.getElementById('regOrden').value);
    const metodo = document.getElementById('regMetodo').value;
    const monto = parseFloat(document.getElementById('regMonto').value);
    if (!idOrden || !monto) return mostrarMensaje('Orden y monto son requeridos', true);
    try {
      const res = await fetch('/api/admin/pagos/registrar', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id_orden: idOrden, metodo, monto })
      });
      const data = await res.json();
      if (!res.ok) { mostrarMensaje('Error al registrar: ' + JSON.stringify(data), true); return; }
      mostrarMensaje('Pago registrado');
      listarTodos();
    } catch (e) { mostrarMensaje(e.message, true); }
  });

  // Crear orden/pago manual por curso
  document.getElementById('btnCrearManualCurso').addEventListener('click', async () => {
    const idEst = parseInt(document.getElementById('manualEstudianteId').value);
    const idCurso = parseInt(document.getElementById('manualCursoId').value);
    const metodo = document.getElementById('manualMetodo').value;
    if (!idEst || !idCurso) return mostrarMensaje('ID estudiante y curso son requeridos', true);
    try {
      const res = await fetch('/api/admin/pagos/crear-manual-curso', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id_estudiante: idEst, id_curso: idCurso, metodo_pago: metodo })
      });
      const data = await res.json();
      if (!res.ok) { mostrarMensaje('Error creando pago manual: ' + JSON.stringify(data), true); return; }
      mostrarMensaje('Pago manual creado (via ' + (data.via||'unknown') + '). Orden: ' + data.id_orden + ' Pago: ' + data.id_pago);
      listarTodos();
    } catch (e) { mostrarMensaje(e.message, true); }
  });

  // initial load
  listarTodos();
});
