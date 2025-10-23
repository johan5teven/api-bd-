package com.project.project.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.repository.CursoRepository;
import com.project.project.repository.LogMongoRepository;
import com.project.project.repository.PagoRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private CursoRepository cursoRepo;
    @Autowired private PagoRepository pagoRepo;
    @Autowired private LogMongoRepository logRepo;

    @GetMapping("/usuarios/resumen")
    public Map<String, Object> resumenUsuarios() {
        Map<String, Object> data = new HashMap<>();
        long total = usuarioRepo.count();
        long estudiantes = usuarioRepo.findAll().stream().filter(u -> u.getRol() == com.project.project.model.Usuario.Rol.estudiante).count();
        long tutores = usuarioRepo.findAll().stream().filter(u -> u.getRol() == com.project.project.model.Usuario.Rol.tutor).count();
        long admins = usuarioRepo.findAll().stream().filter(u -> u.getRol() == com.project.project.model.Usuario.Rol.admin).count();
        data.put("total", total);
        data.put("estudiantes", estudiantes);
        data.put("tutores", tutores);
        data.put("admins", admins);
        return data;
    }

    @GetMapping("/cursos/resumen")
    public Map<String, Object> resumenCursos() {
        Map<String, Object> data = new HashMap<>();
        // count by enum values on Curso
        long activos = cursoRepo.findAll().stream().filter(c -> c.getEstado() == com.project.project.model.Curso.Estado.activo).count();
        long inactivos = cursoRepo.findAll().stream().filter(c -> c.getEstado() == com.project.project.model.Curso.Estado.inactivo).count();
        data.put("activos", activos);
        data.put("inactivos", inactivos);
        return data;
    }

    @GetMapping("/pagos/resumen")
    public Map<String, Object> resumenPagos() {
        Map<String, Object> data = new HashMap<>();

        // Get latest 5 pagos ordered by fecha_pago (descending) - perform in-memory sort to avoid JPA property path issues with underscore names
        List<com.project.project.model.Pago> pagosPage = pagoRepo.findAll().stream()
                .sorted((a, b) -> {
                    try {
                        LocalDate fa = a.getFecha_pago();
                        LocalDate fb = b.getFecha_pago();
                        if (fa == null && fb == null) return 0;
                        if (fa == null) return 1;
                        if (fb == null) return -1;
                        return fb.compareTo(fa); // descending
                    } catch (Exception ex) {
                        return 0;
                    }
                })
                .limit(5)
                .toList();

        // Map to simple DTOs expected by the frontend
        List<Map<String, Object>> ultimos = pagosPage.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id_pago", p.getId_pago());
            try {
                Object orden = p.getOrden();
                if (orden != null) {
                    try {
                        Object usuario = orden.getClass().getMethod("getUsuario").invoke(orden);
                        if (usuario != null) {
                            Object correo = usuario.getClass().getMethod("getCorreo").invoke(usuario);
                            m.put("usuario", correo != null ? correo : usuario.toString());
                        }
                    } catch (NoSuchMethodException nsme) {
                        m.put("usuario", orden.toString());
                    } catch (ReflectiveOperationException roe) {
                        // reflection failed, ignore
                    }
                }
            } catch (RuntimeException rte) {
                // defensive: ignore unexpected runtime exceptions retrieving orden
            }
            m.put("monto", p.getMonto());
            // Pago doesn't expose a public getEstado() in the model in some versions; access defensively
            try { Object estado = p.getClass().getMethod("getEstado").invoke(p); m.put("estado", estado); } catch (ReflectiveOperationException e) { m.put("estado", null); }
            m.put("fecha", p.getFecha_pago());
            return m;
        }).toList();

        double total = pagoRepo.findAll().stream()
                .filter(p -> {
                    try {
                        Object est = p.getClass().getMethod("getEstado").invoke(p);
                        return est != null && "exitoso".equalsIgnoreCase(est.toString());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(p -> p.getMonto() == null ? 0.0 : Double.valueOf(p.getMonto())).sum();

        data.put("ultimos", ultimos);
        data.put("totalVentas", total);
        data.put("meses", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"));
        data.put("valores", List.of(1200, 900, 1100, 1500, 1700, 1300)); // mock data
        return data;
    }

    @GetMapping("/logs/ultimos")
    public List<Map<String, Object>> ultimosLogs() {
        // Use Mongo repository sorting and limit in-memory
        return logRepo.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha")).stream()
                .limit(10)
                .map(l -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("fecha", l.getFecha());
                    m.put("mensaje", l.getDescripcion());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
