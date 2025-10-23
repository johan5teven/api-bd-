package com.project.project.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.repository.CursoRepository;
import com.project.project.repository.InscripcionRepository;
import com.project.project.repository.TutorRepository;

@RestController
@RequestMapping("/api/tutor")
public class TutorDashboardController {

    @Autowired private TutorRepository tutorRepo;
    @Autowired private CursoRepository cursoRepo;
    @Autowired private InscripcionRepository insRepo;

    @GetMapping("/dashboard")
    public Object dashboard(@RequestParam("tutorId") Integer tutorId) {
        var cursos = cursoRepo.findAll().stream().filter(c -> c.getTutor() != null && c.getTutor().getId_tutor().equals(tutorId)).map(c -> {
            Map<String,Object> cm = new HashMap<>();
            cm.put("id", c.getId_curso());
            cm.put("titulo", c.getTitulo());
            cm.put("descripcion", c.getDescripcion());
            var ins = insRepo.findAll().stream().filter(i -> i.getCurso() != null && i.getCurso().getId_curso().equals(c.getId_curso())).map(i -> {
                Map<String,Object> im = new HashMap<>();
                im.put("id_inscripcion", i.getId_inscripcion());
                im.put("estudiante_id", i.getEstudiante() != null ? i.getEstudiante().getId_estudiante() : null);
                im.put("estudiante_correo", i.getEstudiante() != null && i.getEstudiante().getUsuario() != null ? i.getEstudiante().getUsuario().getCorreo() : null);
                im.put("estado", i.getEstado());
                // proxy progress: finalizado -> 100 else 0
                im.put("progreso", i.getEstado() != null && i.getEstado().toString().equalsIgnoreCase("finalizado") ? 100 : 0);
                return im;
            }).collect(Collectors.toList());
            cm.put("inscripciones", ins);
            return cm;
        }).collect(Collectors.toList());
        return cursos;
    }
}
