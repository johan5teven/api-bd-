document.addEventListener("DOMContentLoaded", () => {
    listarCursos();

    document.getElementById("formCurso").addEventListener("submit", async (e) => {
        e.preventDefault();

        const curso = {
            titulo: document.getElementById("titulo").value,
            descripcion: document.getElementById("descripcion").value,
            modalidad: document.getElementById("modalidad").value,
            fecha_inicio: document.getElementById("fecha_inicio").value,
            fecha_fin: document.getElementById("fecha_fin").value,
            precio: parseFloat(document.getElementById("precio").value),
            id_tutor: parseInt(document.getElementById("id_tutor").value),
            estado: "activo"
        };

        const resp = await fetch("/api/admin/cursos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(curso)
        });

        if (resp.ok) {
            alert("✅ Curso creado correctamente");
            e.target.reset();
            listarCursos();
        } else {
            alert("❌ Error al crear el curso");
        }
    });
});

async function listarCursos() {
    const tabla = document.querySelector("#tablaCursos tbody");
    tabla.innerHTML = "";

    const resp = await fetch("/api/admin/cursos");
    const cursos = await resp.json();

    cursos.forEach(curso => {
        const fila = `
            <tr>
                <td>${curso.id_curso}</td>
                <td>${curso.titulo}</td>
                <td>${curso.modalidad}</td>
                <td>${curso.fecha_inicio}</td>
                <td>${curso.fecha_fin}</td>
                <td>$${curso.precio.toFixed(2)}</td>
                <td>${curso.estado}</td>
                <td>${curso.id_tutor}</td>
                <td>
                    <button onclick="eliminarCurso(${curso.id_curso})">Eliminar</button>
                </td>
            </tr>`;
        tabla.insertAdjacentHTML("beforeend", fila);
    });
}

async function eliminarCurso(id) {
    if (confirm("¿Seguro que deseas eliminar este curso?")) {
        const resp = await fetch(`/api/admin/cursos/${id}`, { method: "DELETE" });
        if (resp.ok) {
            alert("✅ Curso eliminado correctamente");
            listarCursos();
        } else {
            alert("❌ Error al eliminar el curso");
        }
    }
}

