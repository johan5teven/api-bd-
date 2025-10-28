package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.project.repository.OrdenRepository;
import com.project.project.repository.PagoRepository;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.UsuarioRepository;
import com.project.project.model.Orden;
import com.project.project.model.Pago;
import com.project.project.model.Curso;
import com.project.project.model.Usuario;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/pagos")
public class AdminPagoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Map<String, Object>>> listarPorUsuario(@PathVariable("id") Integer id) {
        try {
            // Try using stored procedure pkg_pago_listar_por_usuario
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_pago_listar_por_usuario");
            Map<String, Object> in = new HashMap<>();
            in.put("p_id_usuario", id);
            Map<String, Object> out = call.execute(in);
            // The driver may return the result set under '#result-set-1'
            Object rs = out.get("#result-set-1");
            if (rs instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rows = (List<Map<String, Object>>) rs;
                return ResponseEntity.ok(rows);
            }
            // Fallback: run direct query
            String sql = "SELECT p.id_pago, o.id_orden, p.metodo, p.fecha_pago, p.monto, p.estado FROM pago p JOIN orden o ON p.id_orden = o.id_orden WHERE o.id_usuario = ?";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Map<String, Object>>> listarTodos() {
        try {
            String sql = "SELECT p.id_pago, o.id_orden, p.metodo, p.fecha_pago, p.monto, p.estado, o.id_usuario FROM pago p JOIN orden o ON p.id_orden = o.id_orden ORDER BY p.fecha_pago DESC";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }

    public static record PagoRegistroReq(Integer id_orden, String metodo, Double monto) {}

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarPago(@RequestBody PagoRegistroReq req) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_pago_registrar");
            Map<String, Object> in = new HashMap<>();
            in.put("p_id_orden", req.id_orden);
            in.put("p_metodo", req.metodo);
            in.put("p_monto", req.monto);
            Map<String, Object> out = call.execute(in);
            return ResponseEntity.ok(Map.of("success", true, "out", out));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    public static record ManualPagoCursoReq(Integer id_estudiante, Integer id_curso, String metodo_pago) {}

    @Autowired private OrdenRepository ordenRepo;
    @Autowired private PagoRepository pagoRepo;
    @Autowired private CursoRepository cursoRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    /**
     * Crear orden y pago manual para un curso. Intenta llamar al procedimiento CrearOrdenPagoManualCurso
     * y si falla realiza la creación vía JPA (fallback).
     */
    @PostMapping("/crear-manual-curso")
    public ResponseEntity<?> crearPagoManualCurso(@RequestBody ManualPagoCursoReq req) {
        if (req.id_estudiante == null || req.id_curso == null || req.metodo_pago == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "id_estudiante, id_curso y metodo_pago son requeridos"));
        }
        try {
            // Try calling the stored procedure first
            try {
                jdbcTemplate.update("CALL CrearOrdenPagoManualCurso(?,?,?)", req.id_estudiante, req.id_curso, req.metodo_pago);

                // Attempt to find the created order and payment (by today's date and user)
                Integer idOrden = jdbcTemplate.queryForObject(
                        "SELECT id_orden FROM Orden WHERE id_usuario = ? AND fecha_orden = CURDATE() ORDER BY id_orden DESC LIMIT 1",
                        Integer.class, req.id_estudiante);
                Integer idPago = jdbcTemplate.queryForObject(
                        "SELECT id_pago FROM Pago WHERE id_orden = ? ORDER BY id_pago DESC LIMIT 1",
                        Integer.class, idOrden);
                return ResponseEntity.ok(Map.of("id_orden", idOrden, "id_pago", idPago, "via", "sp"));
            } catch (Exception spEx) {
                // SP not available or failed; fall through to JPA fallback
                System.err.println("CrearOrdenPagoManualCurso SP failed: " + spEx.getMessage());
            }

            // Fallback: create via JPA
            Usuario u = usuarioRepo.findById(req.id_estudiante).orElse(null);
            if (u == null) return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            Curso c = cursoRepo.findById(req.id_curso).orElse(null);
            if (c == null) return ResponseEntity.status(404).body(Map.of("error", "Curso no encontrado"));

            Orden o = new Orden();
            o.setUsuario(u);
            o.setFecha_orden(LocalDate.now());
            o.setTotal(c.getPrecio());
            o.setEstado(Orden.Estado.pendiente);
            ordenRepo.saveAndFlush(o);

            Pago p = new Pago();
            p.setOrden(o);
            try { p.setMetodo(Pago.Metodo.valueOf(req.metodo_pago)); } catch (Exception ex) { p.setMetodo(Pago.Metodo.otro); }
            p.setFecha_pago(LocalDate.now());
            p.setMonto(c.getPrecio());
            p.setEstado(Pago.Estado.exitoso);
            pagoRepo.save(p);

            return ResponseEntity.ok(Map.of("id_orden", o.getId_orden(), "id_pago", p.getId_pago(), "via", "jpa"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage(), "exception", e.getClass().getName()));
        }
    }
}
