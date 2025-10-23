document.addEventListener("DOMContentLoaded", () => {
    cargarDashboard();
});

async function cargarDashboard() {
    try {
        // Llamadas a tus endpoints REST del backend
        const [usuariosRes, cursosRes, pagosRes, logsRes] = await Promise.all([
            fetch("/api/admin/usuarios/resumen"),
            fetch("/api/admin/cursos/resumen"),
            fetch("/api/admin/pagos/resumen"),
            fetch("/api/admin/logs/ultimos")
        ]);

        const usuarios = await usuariosRes.json();
        const cursos = await cursosRes.json();
        const pagos = await pagosRes.json();
        const logs = await logsRes.json();

        // Datos de usuarios
        document.getElementById("totalUsuarios").textContent = usuarios.total;
        document.getElementById("estudiantes").textContent = usuarios.estudiantes;
        document.getElementById("tutores").textContent = usuarios.tutores;
        document.getElementById("admins").textContent = usuarios.admins;

        // Cursos
        document.getElementById("cursosActivos").textContent = cursos.activos;
        document.getElementById("cursosInactivos").textContent = cursos.inactivos;

        // Ventas totales
        document.getElementById("ventasTotales").textContent = pagos.totalVentas.toFixed(2);

        // Últimos pagos
        const tablaPagos = document.querySelector("#tablaPagos tbody");
        tablaPagos.innerHTML = "";
        pagos.ultimos.forEach(p => {
            const fila = `
                <tr>
                    <td>${p.id_pago}</td>
                    <td>${p.usuario}</td>
                    <td>$${p.monto.toFixed(2)}</td>
                    <td>${p.estado}</td>
                    <td>${p.fecha}</td>
                </tr>`;
            tablaPagos.insertAdjacentHTML("beforeend", fila);
        });

        // Actividad reciente
        const lista = document.getElementById("actividadReciente");
        lista.innerHTML = "";
        logs.forEach(l => {
            const item = `<li>[${l.fecha}] ${l.mensaje}</li>`;
            lista.insertAdjacentHTML("beforeend", item);
        });

        // Gráfico simple (ventas mensuales)
        const ctx = document.getElementById("graficoVentas").getContext("2d");
        new Chart(ctx, {
            type: "bar",
            data: {
                labels: pagos.meses,
                datasets: [{
                    label: "Ventas ($)",
                    data: pagos.valores,
                    backgroundColor: "rgba(75, 192, 192, 0.5)"
                }]
            }
        });

    } catch (error) {
        console.error("Error cargando dashboard:", error);
    }
}
