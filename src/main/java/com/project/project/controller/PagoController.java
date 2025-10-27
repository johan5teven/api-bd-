package com.project.project.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Pago;
import com.project.project.repository.InscripcionRepository;
import com.project.project.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InscripcionRepository inscripcionRepo;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listar() {
        return pagoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Pago buscarPorId(@PathVariable Integer id) {
        return pagoService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Pago crear(@RequestBody Pago pago) {
        return pagoService.guardar(pago);
    }

    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Integer id, @RequestBody Pago pago) {
        pago.setId_pago(id);
        return pagoService.guardar(pago);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        pagoService.eliminar(id);
    }

    // Call stored procedure to register a payment
    @PostMapping("/registrar")
    public Map<String, Object> registrar(@RequestBody Map<String, Object> body) {
        Integer idOrden = body.get("id_orden") == null ? null : Integer.parseInt(body.get("id_orden").toString());
        String metodo = (String) body.get("metodo");
        Double monto = body.get("monto") == null ? 0.0 : Double.parseDouble(body.get("monto").toString());

        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_pago_registrar");
        Map<String, Object> in = Map.of("p_id_orden", idOrden, "p_metodo", metodo, "p_monto", monto);
        Map<String, Object> out = call.execute(in);
        return Map.of("status", "ok", "result", out);
    }

    // List payments for a user using stored procedure pkg_pago_listar_por_usuario
    @GetMapping("/usuario/{id}")
    public List<Map<String, Object>> listarPorUsuario(@PathVariable Integer id) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_pago_listar_por_usuario");
            Map<String, Object> in = Map.of("p_id_usuario", id);
            Map<String, Object> out = call.execute(in);
            Object rs = out.get("#result-set-1");
            if (rs instanceof List) return (List<Map<String, Object>>) rs;
        } catch (Exception e) {
            // fallback to manual query
        }

        String sql = "SELECT p.id_pago, o.id_orden, p.metodo, p.fecha_pago, p.monto, p.estado FROM pago p JOIN orden o ON p.id_orden = o.id_orden WHERE o.id_usuario = ?";
        return jdbcTemplate.queryForList(sql, id);
    }

    // Return per-course spending (based on inscripciones) and overall total (calls stored procedure TotalGastadoEnCursos)
    @GetMapping("/resumen-usuario/{id}")
    public Map<String, Object> resumenPorUsuario(@PathVariable Integer id) {
        // per-course: use Inscripcion records to list courses the estudiante bought and their price
    List<Map<String,Object>> porCurso = inscripcionRepo.findAll().stream()
        .filter(i -> i.getEstudiante() != null && i.getEstudiante().getUsuario() != null && i.getEstudiante().getUsuario().getId_usuario().equals(id))
        .map(i -> Map.<String,Object>of(
            "id_curso", i.getCurso() != null ? i.getCurso().getId_curso() : null,
            "titulo", i.getCurso() != null ? i.getCurso().getTitulo() : null,
            "monto_pagado", i.getCurso() != null && i.getCurso().getPrecio() != null ? i.getCurso().getPrecio() : 0.0,
            "fecha_inscripcion", i.getFecha_inscripcion()
        )).collect(Collectors.toList());

        double totalByCourses = porCurso.stream().mapToDouble(m -> ((Number) m.getOrDefault("monto_pagado", 0)).doubleValue()).sum();

        // call stored procedure TotalGastadoEnCursos(IN p_id_usuario INT)
        Double totalGastado = null;
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("TotalGastadoEnCursos");
            Map<String,Object> in = Map.of("p_id_usuario", id);
            Map<String,Object> out = call.execute(in);
            Object rs = out.get("#result-set-1");
            if (rs instanceof List && !((List<?>) rs).isEmpty()) {
                Object first = ((List<?>) rs).get(0);
                if (first instanceof Map) {
                    Object v = ((Map<?,?>) first).get("total_gastado");
                    if (v instanceof Number) totalGastado = ((Number) v).doubleValue();
                }
            }
        } catch (Exception e) {
            // fallback: compute total successful pagos for user's orders
            try {
                String sql = "SELECT IFNULL(SUM(p.monto),0) FROM pago p JOIN orden o ON p.id_orden = o.id_orden WHERE o.id_usuario = ? AND p.estado = 'exitoso'";
                Number n = jdbcTemplate.queryForObject(sql, Number.class, id);
                totalGastado = n != null ? n.doubleValue() : 0.0;
            } catch (Exception ex) {
                totalGastado = 0.0;
            }
        }

        return Map.of(
                "por_curso", porCurso,
                "total_by_courses", totalByCourses,
                "total_gastado", totalGastado
        );
    }
}
