package com.project.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/inscripciones")
public class AdminInscripcionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Map<String, Object>> listar() {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_inscripcion_listar");
            Map<String, Object> in = Map.of();
            Map<String, Object> out = call.execute(in);
            Object rs = out.get("#result-set-1");
            if (rs instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> list = (List<Map<String, Object>>) rs;
                return list;
            }
        } catch (Exception e) {
            // fallthrough to fallback
        }

        // Fallback: run equivalent query
        String sql = "SELECT i.id_inscripcion, e.id_estudiante, u.nombre, c.titulo, i.fecha_inscripcion, i.estado "
                + "FROM inscripcion i JOIN estudiante e ON i.id_estudiante = e.id_estudiante "
                + "JOIN usuario u ON e.id_estudiante = u.id_usuario JOIN curso c ON i.id_curso = c.id_curso";
        return jdbcTemplate.queryForList(sql);
    }

    @PostMapping("/inscribir")
    public Map<String, Object> inscribir(@RequestBody Map<String, Object> body) {
        try {
            Integer idEstudiante = body.get("id_estudiante") == null ? null : Integer.parseInt(body.get("id_estudiante").toString());
            Integer idCurso = body.get("id_curso") == null ? null : Integer.parseInt(body.get("id_curso").toString());
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_inscripcion_inscribir");
            Map<String, Object> in = Map.of("p_id_estudiante", idEstudiante, "p_id_curso", idCurso);
            Map<String, Object> out = call.execute(in);
            return Map.of("status", "ok", "result", out);
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @PostMapping("/cancelar")
    public Map<String, Object> cancelar(@RequestBody Map<String, Object> body) {
        try {
            Integer idInscripcion = body.get("id_inscripcion") == null ? null : Integer.parseInt(body.get("id_inscripcion").toString());
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("pkg_inscripcion_cancelar");
            Map<String, Object> in = Map.of("p_id_inscripcion", idInscripcion);
            Map<String, Object> out = call.execute(in);
            return Map.of("status", "ok", "result", out);
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
