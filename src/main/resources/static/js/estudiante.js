document.addEventListener('DOMContentLoaded', async () => {
  const el = id => document.getElementById(id);
  console.log('estudiante.js loaded on', location.pathname);
  // guard to prevent duplicate loads if script is included twice
  if (window.__estudiante_loaded) return; window.__estudiante_loaded = true;
  async function load() {
    try {
      const res = await fetch('/api/estudiante/servicios');
      if (!res.ok) throw new Error('Error cargando servicios');
      const data = await res.json();
      renderGrid(el('cursos'), data.cursos || [], item => `
        <div class="card">
          <h3>${item.titulo}</h3>
          <p>${item.descripcion ?? ''}</p>
          <p><strong>Precio:</strong> ${item.precio ?? 'Gratis'}</p>
          <p><button data-curso-id="${item.id}" class="btnComprar" onclick="if(window.comprarCursoHelper) window.comprarCursoHelper({ estudianteId: parseInt(document.getElementById('estId')?.value||0), cursoId: ${item.id}, button:this })">Comprar curso</button></p>
        </div>`);

      renderGrid(el('productos'), data.productos || [], item => `
        <div class="card">
          <h3>${item.titulo}</h3>
          <p>${item.descripcion ?? ''}</p>
          <p><strong>Precio:</strong> ${item.precio ?? '---'}</p>
        </div>`);

      renderGrid(el('materiales'), data.materiales || [], item => `
        <div class="card">
          <h3>${item.titulo}</h3>
          <p>${item.descripcion ?? ''}</p>
          ${item.url ? `<p><a href="${item.url}" target="_blank">Ver material</a></p>` : ''}
        </div>`);

      renderGrid(el('guias'), data.guias || [], item => `
        <div class="card">
          <h3>${item.titulo}</h3>
          <p>${item.descripcion ?? ''}</p>
          <p><a href="${item.url}" target="_blank">Leer guía</a></p>
        </div>`);

    } catch (e) { console.error(e); alert('No se pudieron cargar los servicios.'); }
  }

  function renderGrid(container, items, tpl) {
    if (!container) {
      console.warn('renderGrid: container not found, skipping render', { container, itemsCount: (items || []).length });
      return;
    }
    container.innerHTML = '';
    if (!items || items.length === 0) { container.innerHTML = '<p>No hay elementos.</p>'; return; }
    items.forEach(it => { const div = document.createElement('div'); div.innerHTML = tpl(it); container.appendChild(div); });
  }

  try {
    load();
  } catch (err) {
    console.error('Error during load()', err);
  }
  // load tutors into select
  try {
    const tRes = await fetch('/api/estudiante/tutores');
    const tutors = await tRes.json();
    const selTutor = document.getElementById('selTutor');
    tutors.forEach(t => { const opt = document.createElement('option'); opt.value = t.id; opt.text = (t.nombre || t.correo) + (t.especialidad ? ' - ' + t.especialidad : ''); selTutor.appendChild(opt); });
  } catch(e){console.warn('no tutors', e)}

  // If the page contains a dedicated btnComprar (e.g. agendamiento form), attach handler; otherwise skip
  const btnComprarEl = document.getElementById('btnComprar');
  if (btnComprarEl) {
    btnComprarEl.addEventListener('click', async () => {
      const estudianteId = parseInt(document.getElementById('estId').value);
      const tutorId = parseInt(document.getElementById('selTutor').value);
      const tier = document.getElementById('selTier').value;
      if (!estudianteId || !tutorId) { alert('Ingrese su id de estudiante y seleccione tutor'); return; }
      try {
        const res = await fetch('/api/estudiante/comprar-agendamiento', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ estudianteId, tutorId, tier }) });
        const j = await res.json();
        if (res.ok) { alert('Compra realizada, orden: ' + j.id_orden); loadAdquiridos(estudianteId); }
        else alert('Error: ' + (j.error || JSON.stringify(j)));
      } catch(e){ console.error(e); alert('Error al comprar'); }
    });
  } else {
    console.debug('No #btnComprar element present on this page, skipping that handler');
  }

  // delegate click for course purchase buttons
  document.addEventListener('click', async (ev) => {
    const btn = ev.target.closest && ev.target.closest('.btnComprar');
    // Log every button click (only UI-level, non-sensitive)
    try{ const bt = ev.target.closest && ev.target.closest('button'); if(bt){
        const payload = { tabla:'ui', operacion:'click', descripcion: 'button click', detalle: { text: bt.innerText, id: bt.id, classes: bt.className } };
        navigator.sendBeacon && navigator.sendBeacon('/api/logs', JSON.stringify(payload));
        // fallback
        fetch('/api/logs', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) }).catch(()=>{});
      }
    }catch(e){}
    if (!btn) return;
    console.log('buy button clicked', btn);
    const cursoId = parseInt(btn.getAttribute('data-curso-id'));
  const estudianteInput = document.getElementById('estId');
  // allow empty estId in the page: we'll try to resolve it from the session if missing
  let estudianteId = estudianteInput ? parseInt(estudianteInput.value) : NaN;
  if (!Number.isInteger(estudianteId) || estudianteId <= 0) estudianteId = null;
    if (!cursoId) { alert('Curso inválido'); return; }
    // change button text to indicate it was pressed
    try{ btn.dataset.origText = btn.innerText; btn.innerText = 'Se oprimió'; }catch(e){}
    // delegate to a small helper so we can reuse it in tests
    await comprarCursoHandler({ estudianteId, cursoId, button: btn });
  });

  async function comprarCursoHandler({ estudianteId, cursoId, button } = {}){
    try{
      console.log('comprarCursoHandler', { estudianteId, cursoId });
      const dbg = document.getElementById('debugLast'); if (dbg) dbg.innerText = 'comprarCursoHandler ' + JSON.stringify({ estudianteId, cursoId });
      // if estudianteId not provided, try to resolve from session endpoint
      if (!estudianteId) {
        try {
          const s = await fetch('/api/auth/session');
          if (s.ok) {
            const sj = await s.json();
            if (sj && sj.authenticated && sj.id) {
              estudianteId = parseInt(sj.id);
              if (dbg) dbg.innerText = 'resolved estudianteId from session: ' + estudianteId;
            }
          }
        } catch(e) { console.warn('session fetch failed', e); }
      }
      if (!estudianteId) { alert('No se pudo determinar el id de estudiante. Inicie sesión o complete el campo id.'); return; }

      const res = await fetch('/api/estudiante/comprar-curso', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ estudianteId, cursoId }) });
      const j = await res.json().catch(()=>({}));
      console.log('comprar-curso response', res.status, j);
      if (dbg) dbg.innerText = 'comprar-curso response ' + res.status + ' ' + JSON.stringify(j);
      if (res.ok){
        const ordenId = j.id_orden || j.id || j.orderId || j.idOrden;
        if (ordenId){ window.location.href = '/estudiante-pagos.html?ordenId=' + encodeURIComponent(ordenId) + '&estudianteId=' + encodeURIComponent(estudianteId); return; }
        alert('Compra registrada, pero no se devolvió el id de la orden. Verifique en Pagos.');
        loadAdquiridos(estudianteId);
        return;
      }
      alert('Error: ' + (j.error || JSON.stringify(j)));
    }catch(e){ console.error('Error al comprar curso', e); alert('Error al comprar curso'); }
  }
  // expose helper to global so inline onclick can call it and tests can call it
  window.comprarCursoHelper = comprarCursoHandler;

  async function loadAdquiridos(estudianteId){
    try{
      const r = await fetch('/api/estudiante/adquiridos?estudianteId=' + encodeURIComponent(estudianteId));
      const data = await r.json();
      const el = document.getElementById('adquiridos');
      if (!el) { console.warn('loadAdquiridos: #adquiridos element not found'); return; }
      el.innerHTML = '';
      if ((data.ordenes || []).length) {
        const h = document.createElement('div'); h.innerHTML = '<h3>Órdenes</h3>'; el.appendChild(h);
        data.ordenes.forEach(o => { const d = document.createElement('div'); d.className='card'; d.innerHTML=`<strong>#${o.id}</strong> ${o.fecha} - ${o.total} (${o.estado})`; el.appendChild(d); });
      }
      if ((data.agendamientos || []).length) {
        const h2 = document.createElement('div'); h2.innerHTML = '<h3>Agendamientos</h3>'; el.appendChild(h2);
        data.agendamientos.forEach(a=>{ const d = document.createElement('div'); d.className='card'; d.innerHTML=`<strong>#${a.id}</strong> Tutor: ${a.tutor} Estado: ${a.estado}`; el.appendChild(d); })
      }
    }catch(e){console.error(e)}
  }
});
