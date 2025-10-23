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
}
