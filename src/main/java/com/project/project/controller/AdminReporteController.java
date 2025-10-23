package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.repository.LogMongoRepository;

@RestController
@RequestMapping("/api/admin/reportes")
public class AdminReporteController {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private LogMongoRepository logRepo;

    @GetMapping("/usuarios/serie")
    public List<Map<String, Object>> usuariosSerie() {
        String sql = "SELECT MONTH(fecha_registro) as mes_num, COUNT(*) as valor FROM usuario GROUP BY MONTH(fecha_registro) ORDER BY mes_num";
        return jdbc.query(sql, (rs, i) -> {
            Map<String,Object> m = new HashMap<>();
            int mesNum = rs.getInt("mes_num");
            m.put("mes", monthLabel(mesNum));
            m.put("valor", rs.getInt("valor"));
            return m;
        });
    }

    @GetMapping("/ventas/serie")
    public List<Map<String, Object>> ventasSerie() {
        String sql = "SELECT MONTH(fecha_pago) as mes_num, SUM(monto) as valor FROM pago GROUP BY MONTH(fecha_pago) ORDER BY mes_num";
        return jdbc.query(sql, (rs, i) -> {
            Map<String,Object> m = new HashMap<>();
            int mesNum = rs.getInt("mes_num");
            m.put("mes", monthLabel(mesNum));
            m.put("valor", rs.getDouble("valor"));
            return m;
        });
    }

    @GetMapping("/actividad/reciente")
    public List<Map<String, Object>> actividadReciente() {
        return logRepo.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha")).stream()
            .limit(20)
            .map(l -> {
                Map<String,Object> m = new HashMap<>();
                m.put("fecha", l.getFecha());
                m.put("mensaje", l.getDescripcion());
                return m;
            })
            .toList();
    }

    private String monthLabel(int mes) {
        return switch (mes) {
            case 1 -> "Ene"; case 2 -> "Feb"; case 3 -> "Mar"; case 4 -> "Abr"; case 5 -> "May"; case 6 -> "Jun";
            case 7 -> "Jul"; case 8 -> "Ago"; case 9 -> "Sep"; case 10 -> "Oct"; case 11 -> "Nov"; case 12 -> "Dic";
            default -> String.valueOf(mes);
        };
    }
}
