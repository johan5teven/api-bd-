document.addEventListener('DOMContentLoaded', () => {
    cargarInscripciones();

    document.getElementById('formInscribir').addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            id_estudiante: parseInt(document.getElementById('id_estudiante').value),
            id_curso: parseInt(document.getElementById('id_curso').value)
        };
        const resp = await fetch('/api/admin/inscripciones/inscribir', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
        });
        const body = await resp.json();
        if (resp.ok && body.status === 'ok') {
            alert('Estudiante inscrito correctamente');
            cargarInscripciones();
            e.target.reset();
        } else {
            alert('Error: ' + (body.message || JSON.stringify(body)));
        }
    });
});

async function cargarInscripciones() {
    const tabla = document.querySelector('#tablaInscripciones tbody');
    tabla.innerHTML = '';
    try {
        const resp = await fetch('/api/admin/inscripciones');
        const lista = await resp.json();
        lista.forEach(i => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${i.id_inscripcion}</td>
                <td>${i.id_estudiante}</td>
                <td>${i.nombre}</td>
                <td>${i.titulo}</td>
                <td>${i.fecha_inscripcion}</td>
                <td>${i.estado}</td>
                <td><button data-id="${i.id_inscripcion}" class="btn-cancel">Cancelar</button></td>
            `;
            tabla.appendChild(tr);
        });

        document.querySelectorAll('.btn-cancel').forEach(b => b.addEventListener('click', async (ev) => {
            const id = ev.target.getAttribute('data-id');
            if (!confirm('¿Cancelar esta inscripción?')) return;
            const resp = await fetch('/api/admin/inscripciones/cancelar', {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id_inscripcion: parseInt(id) })
            });
            const body = await resp.json();
            if (resp.ok && body.status === 'ok') {
                alert('Inscripción cancelada');
                cargarInscripciones();
            } else {
                alert('Error: ' + (body.message || JSON.stringify(body)));
            }
        }));

    } catch (e) {
        console.error(e);
        alert('Error cargando inscripciones');
    }
}
