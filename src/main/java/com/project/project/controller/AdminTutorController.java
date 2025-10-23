package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Tutor;
import com.project.project.model.Usuario;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.TutorRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin/tutores")
public class AdminTutorController {

    @Autowired private TutorRepository tutorRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private CursoRepository cursoRepo;

    @GetMapping("/listar")
    public List<Map<String,Object>> listar() {
        return tutorRepo.findAll().stream().map(t -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id_tutor", t.getId_tutor());
            m.put("nombre", t.getUsuario() != null ? t.getUsuario().getNombre() : null);
            m.put("correo", t.getUsuario() != null ? t.getUsuario().getCorreo() : null);
            m.put("especialidad", t.getEspecialidad());
            m.put("activo", t.getUsuario() != null ? t.getUsuario().getEstado() : null);
            return m;
        }).toList();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String,Object> body) {
        // create Usuario + Tutor by correo or link existing usuario
        try {
            String correo = (String) body.get("correo");
            if (correo == null || correo.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","correo is required"));
            Usuario u = usuarioRepo.findByCorreo(correo).orElse(null);
            if (u == null) {
                // create minimal usuario with correo only
                u = new Usuario();
                u.setCorreo(correo);
                u.setNombre((String) body.getOrDefault("nombre", null));
                u.setApellido((String) body.getOrDefault("apellido", null));
                u.setContrasena((String) body.getOrDefault("contrasena", ""));
                u.setRol(Usuario.Rol.tutor);
                u.setEstado(Usuario.Estado.activo);
                u.setFecha_registro(java.time.LocalDate.now());
                usuarioRepo.save(u);
            }
            Tutor t = new Tutor();
            t.setId_tutor(u.getId_usuario());
            t.setUsuario(u);
            t.setEspecialidad((String) body.getOrDefault("especialidad", null));
            tutorRepo.save(t);
            return ResponseEntity.ok(Map.of("id_tutor", t.getId_tutor()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable("id") Integer id) {
        return usuarioRepo.findById(id).map(u -> { u.setEstado(com.project.project.model.Usuario.Estado.activo); usuarioRepo.save(u); return ResponseEntity.ok(Map.of("ok",true)); }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> borrar(@PathVariable("id") Integer id) {
        tutorRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
