package com.project.project.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.repository.BlogRepository;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.InscripcionRepository;
import com.project.project.repository.MaterialRepository;
import com.project.project.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteServiciosController {

    private final CursoRepository cursoRepo;
    private final ProductoRepository productoRepo;
    private final MaterialRepository materialRepo;
    private final BlogRepository blogRepo;
    private final JdbcTemplate jdbcTemplate;
    private final InscripcionRepository inscripcionRepo;

    public EstudianteServiciosController(CursoRepository cursoRepo, ProductoRepository productoRepo, MaterialRepository materialRepo, BlogRepository blogRepo, JdbcTemplate jdbcTemplate, InscripcionRepository inscripcionRepo) {
        this.cursoRepo = cursoRepo;
        this.productoRepo = productoRepo;
        this.materialRepo = materialRepo;
        this.blogRepo = blogRepo;
        this.jdbcTemplate = jdbcTemplate;
        this.inscripcionRepo = inscripcionRepo;
    }

    @GetMapping("/servicios")
    public Map<String,Object> servicios(@RequestParam(required = false) Integer estudianteId, HttpSession session) {
        // resolve estudianteId from param or session, then use a final variable for lambdas
        Integer estId = estudianteId;
        if (estId == null && session != null) {
            Object s = session.getAttribute("usuarioId");
            if (s instanceof Number) estId = ((Number) s).intValue();
            else if (s != null) {
                try { estId = Integer.parseInt(s.toString()); } catch (NumberFormatException e) { estId = null; }
            }
        }
        final Integer resolvedEstudianteId = estId;
        Map<String,Object> out = new HashMap<>();

        List<Map<String,Object>> cursos = cursoRepo.findAll().stream().filter(c -> {
            if (resolvedEstudianteId == null) return true;
            // if student already inscribed, exclude the course
            try {
                return !inscripcionRepo.existsByEstudianteIdAndCursoId(resolvedEstudianteId, c.getId_curso());
            } catch (Exception ex) {
                return true;
            }
        }).map(c -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", c.getId_curso());
            m.put("titulo", c.getTitulo());
            m.put("descripcion", c.getDescripcion());
            m.put("precio", c.getPrecio());
            m.put("estado", c.getEstado());
            if (c.getTutor() != null && c.getTutor().getUsuario() != null) {
                m.put("tutor", Map.of(
                    "id", c.getTutor().getId_tutor(),
                    "nombre", c.getTutor().getUsuario().getNombre(),
                    "correo", c.getTutor().getUsuario().getCorreo(),
                    "especialidad", c.getTutor().getEspecialidad()
                ));
            } else {
                m.put("tutor", null);
            }
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> productos = productoRepo.findAll().stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", p.getId_producto());
            m.put("titulo", p.getNombre());
            m.put("descripcion", p.getDescripcion());
            m.put("precio", p.getPrecio());
            m.put("stock", p.getStock());
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> materiales = materialRepo.findAll().stream().map(mat -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", mat.getId_material());
            m.put("titulo", mat.getTitulo());
            m.put("descripcion", mat.getTipo() != null ? mat.getTipo().name() : null);
            m.put("url", mat.getUrl_archivo());
            if (mat.getCurso() != null) m.put("curso", Map.of("id", mat.getCurso().getId_curso(), "titulo", mat.getCurso().getTitulo()));
            else m.put("curso", null);
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> guias = blogRepo.findAll().stream().map(b -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", b.getId_post());
            m.put("titulo", b.getTitulo());
            m.put("descripcion", b.getContenido() != null ? (b.getContenido().length() > 120 ? b.getContenido().substring(0,120) + "..." : b.getContenido()) : "");
            m.put("url", "/blog/" + b.getId_post());
            return m;
        }).collect(Collectors.toList());

        out.put("cursos", cursos);
        out.put("productos", productos);
        out.put("materiales", materiales);
        out.put("guias", guias);
        return out;
    }

    @GetMapping("/materiales")
    public List<Map<String,Object>> materiales() {
        return materialRepo.findAll().stream().map(mat -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id_material", mat.getId_material());
            m.put("titulo", mat.getTitulo());
            m.put("tipo", mat.getTipo() != null ? mat.getTipo().name() : null);
            m.put("url", mat.getUrl_archivo());
            m.put("curso", mat.getCurso() != null ? Map.of("id", mat.getCurso().getId_curso(), "titulo", mat.getCurso().getTitulo()) : null);
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/mis-cursos")
    public List<Map<String,Object>> misCursos(@RequestParam(required = false) Integer estudianteId, HttpSession session) {
        Integer id = estudianteId;
        if (id == null) {
            Object s = session.getAttribute("usuarioId");
            if (s instanceof Number) id = ((Number) s).intValue();
            else if (s != null) {
                try { id = Integer.parseInt(s.toString()); } catch (Exception e) { id = null; }
            }
        }
        if (id == null) return List.of();

        String sql = "CALL ObtenerCursosPorEstudiante(?)";
        return jdbcTemplate.query(sql, new Object[] { id }, (rs, rowNum) -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id_curso", rs.getInt("id_curso"));
            m.put("titulo", rs.getString("titulo"));
            m.put("modalidad", rs.getString("modalidad"));
            Date d1 = rs.getDate("fecha_inicio");
            Date d2 = rs.getDate("fecha_fin");
            m.put("fecha_inicio", d1 != null ? d1.toLocalDate().toString() : null);
            m.put("fecha_fin", d2 != null ? d2.toLocalDate().toString() : null);
            m.put("estado_curso", rs.getString("estado_curso"));
            m.put("estado_inscripcion", rs.getString("estado_inscripcion"));
            return m;
        });
    }

    @GetMapping("/tutores-por-curso")
    public List<Map<String,Object>> tutoresPorCurso(@RequestParam(required = false) Integer estudianteId, HttpSession session) {
        // Resolve estudiante id from param or session
        Integer id = estudianteId;
        if (id == null && session != null) {
            Object s = session.getAttribute("usuarioId");
            if (s instanceof Number) id = ((Number) s).intValue();
            else if (s != null) {
                try { id = Integer.parseInt(s.toString()); } catch (NumberFormatException ex) { id = null; }
            }
        }
        if (id == null) return List.of();

        try {
            String sql = "CALL DetalleCursosPorEstudiante(?)";
            List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, id);

            // Group rows by course id preserving insertion order
            Map<Object, Map<String,Object>> byCourse = new LinkedHashMap<>();
            for (Map<String,Object> r : rows) {
                Object cursoId = r.get("id_curso");
                if (!byCourse.containsKey(cursoId)) {
                    Map<String,Object> course = new HashMap<>();
                    course.put("id_curso", cursoId);
                    course.put("titulo_curso", r.getOrDefault("titulo_curso", r.get("titulo")));
                    course.put("modalidad", r.get("modalidad"));
                    course.put("fecha_inicio", r.get("fecha_inicio"));
                    course.put("fecha_fin", r.get("fecha_fin"));
                    course.put("estado_curso", r.get("estado_curso"));
                    course.put("contenido", r.getOrDefault("contenido", r.get("descripcion")));

                    // tutor
                    Map<String,Object> tutor = null;
                    if (r.get("id_tutor") != null) {
                        tutor = new HashMap<>();
                        tutor.put("id", r.get("id_tutor"));
                        tutor.put("nombre", r.get("nombre_tutor"));
                        tutor.put("apellido", r.get("apellido_tutor"));
                        tutor.put("especialidad", r.get("especialidad"));
                        tutor.put("experiencia", r.get("experiencia"));
                    }
                    course.put("tutor", tutor);
                    course.put("materiales", new ArrayList<Map<String,Object>>());
                    byCourse.put(cursoId, course);
                }

                // add material if present
                Object mId = r.get("id_material");
                if (mId != null) {
                    Map<String,Object> mat = new HashMap<>();
                    mat.put("id_material", mId);
                    mat.put("tipo", r.getOrDefault("tipo_material", r.get("tipo")));
                    mat.put("titulo", r.getOrDefault("titulo_material", r.get("titulo_material")));
                    mat.put("url_archivo", r.get("url_archivo"));
                    mat.put("fecha_carga", r.get("fecha_carga"));
                    @SuppressWarnings("unchecked")
                    List<Map<String,Object>> list = (List<Map<String,Object>>) byCourse.get(cursoId).get("materiales");
                    list.add(mat);
                }
            }

            // For any course that ended up with no materials from the SP, fallback to JPA fetch
            for (Map<String,Object> course : byCourse.values()) {
                @SuppressWarnings("unchecked")
                List<Map<String,Object>> mats = (List<Map<String,Object>>) course.get("materiales");
                if (mats == null || mats.isEmpty()) {
                    Object cid = course.get("id_curso");
                    Integer cursoIdInt = null;
                    if (cid instanceof Number) cursoIdInt = ((Number) cid).intValue();
                    else if (cid != null) {
                        try { cursoIdInt = Integer.parseInt(cid.toString()); } catch (Exception ex) { cursoIdInt = null; }
                    }
                    if (cursoIdInt != null) {
                        List<com.project.project.model.Material> jpaMats = materialRepo.findByCursoId(cursoIdInt);
                        List<Map<String,Object>> j = new ArrayList<>();
                        for (com.project.project.model.Material mm : jpaMats) {
                            Map<String,Object> mmj = new HashMap<>();
                            mmj.put("id_material", mm.getId_material());
                            mmj.put("tipo", mm.getTipo() != null ? mm.getTipo().name() : null);
                            mmj.put("titulo", mm.getTitulo());
                            mmj.put("url_archivo", mm.getUrl_archivo());
                            mmj.put("fecha_carga", mm.getFecha_carga());
                            j.add(mmj);
                        }
                        course.put("materiales", j);
                    }
                }
            }

            return new ArrayList<>(byCourse.values());
        } catch (Exception e) {
            // If the SP fails, return empty list. Caller can fall back if desired.
            return List.of();
        }
    }

}
