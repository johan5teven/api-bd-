package com.project.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Curso;
import com.project.project.model.Tutor;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.TutorRepository;

@RestController
@RequestMapping("/api/admin/cursos")
public class AdminCursoController {

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private TutorRepository tutorRepo;

    // List all cursos (frontend expects an array of cursos with id_tutor)
    @GetMapping
    public List<Object> listar() {
        return cursoRepo.findAll().stream().map(c -> MapBuilder.fromCurso(c)).collect(Collectors.toList());
    }

    @PostMapping
    public Object crear(@RequestBody java.util.Map<String, Object> body) {
        Curso c = new Curso();
        if (body.containsKey("titulo")) c.setTitulo((String) body.get("titulo"));
        if (body.containsKey("descripcion")) c.setDescripcion((String) body.get("descripcion"));
        if (body.containsKey("modalidad")) {
            try { c.setModalidad(com.project.project.model.Curso.Modalidad.valueOf(((String)body.get("modalidad")))); } catch (Exception e) {}
        }
        if (body.containsKey("fecha_inicio")) {
            try { c.setFecha_inicio(java.time.LocalDate.parse((String) body.get("fecha_inicio"))); } catch (Exception e) {}
        }
        if (body.containsKey("fecha_fin")) {
            try { c.setFecha_fin(java.time.LocalDate.parse((String) body.get("fecha_fin"))); } catch (Exception e) {}
        }
        if (body.containsKey("precio")) {
            try { c.setPrecio(Double.parseDouble(body.get("precio").toString())); } catch (Exception e) {}
        }
        if (body.containsKey("estado")) {
            try { c.setEstado(com.project.project.model.Curso.Estado.valueOf(((String)body.get("estado")))); } catch (Exception e) {}
        } else {
            c.setEstado(com.project.project.model.Curso.Estado.activo);
        }

        // attach tutor if provided
        if (body.containsKey("id_tutor")) {
            try {
                Integer idTutor = Integer.parseInt(body.get("id_tutor").toString());
                Tutor t = tutorRepo.findById(idTutor).orElse(null);
                c.setTutor(t);
            } catch (Exception e) {
                // ignore
            }
        }

    Curso saved = cursoRepo.save(c);
    return MapBuilder.fromCurso(saved);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        cursoRepo.deleteById(id);
    }

    // Simple helper to build response maps without adding a new DTO class
    private static class MapBuilder {
        static java.util.Map<String, Object> fromCurso(Curso c) {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id_curso", c.getId_curso());
            m.put("titulo", c.getTitulo());
            m.put("descripcion", c.getDescripcion());
            m.put("modalidad", c.getModalidad() != null ? c.getModalidad().name() : null);
            m.put("fecha_inicio", c.getFecha_inicio() != null ? c.getFecha_inicio().toString() : null);
            m.put("fecha_fin", c.getFecha_fin() != null ? c.getFecha_fin().toString() : null);
            m.put("precio", c.getPrecio());
            m.put("estado", c.getEstado() != null ? c.getEstado().name() : null);
            m.put("id_tutor", c.getTutor() != null ? c.getTutor().getId_tutor() : null);
            return m;
        }
    }
}
