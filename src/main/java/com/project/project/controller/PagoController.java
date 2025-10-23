package com.project.project.controller;

import java.util.List;
import java.util.Map;

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
import com.project.project.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}
