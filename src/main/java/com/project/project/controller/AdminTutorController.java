package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
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
import com.project.project.repository.TutorRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/admin/tutores")
public class AdminTutorController {

    @Autowired private TutorRepository tutorRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

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
    @Transactional
    public ResponseEntity<?> crear(@RequestBody Map<String,Object> body) {
    try {
            String correo = (String) body.get("correo");
            if (correo == null || correo.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","correo is required"));
            // If a user already exists with that correo, reuse it (do not call SP)
            Usuario existing = usuarioRepo.findByCorreo(correo).orElse(null);
            if (existing != null) {
                Tutor t = new Tutor();
                t.setUsuario(existing);
                t.setEspecialidad((String) body.getOrDefault("especialidad", null));
                tutorRepo.save(t);
                tutorRepo.flush();
                return ResponseEntity.ok(Map.of("id_tutor", t.getId_tutor()));
            }

            // Try calling stored procedure CrearTutor when Usuario doesn't exist.
            try {
                String nombre = (String) body.getOrDefault("nombre", "");
                String apellido = (String) body.getOrDefault("apellido", "");
                String contrasena = (String) body.getOrDefault("contrasena", "");
                String especialidad = (String) body.getOrDefault("especialidad", "");
                Integer experiencia = 0;
                try { experiencia = Integer.parseInt(String.valueOf(body.getOrDefault("experiencia", 0))); } catch (Exception ex) { experiencia = 0; }
                String biografia = (String) body.getOrDefault("biografia", "");

                // CALL CrearTutor(p_nombre, p_apellido, p_correo, p_contrasena, p_especialidad, p_experiencia, p_biografia)
                jdbcTemplate.update("CALL CrearTutor(?,?,?,?,?,?,?)",
                        nombre, apellido, correo, contrasena, especialidad, experiencia, biografia);

                // after calling the procedure, the Usuario should exist; fetch it to get the id
                Usuario u = usuarioRepo.findByCorreo(correo).orElse(null);
                if (u != null) {
                    return ResponseEntity.ok(Map.of("id_tutor", u.getId_usuario()));
                }
                // fallthrough to JPA fallback if SP didn't create the user for any reason
            } catch (Exception spEx) {
                // SP might not exist or failed; fall back to JPA creation below
                // log to console for debugging
                System.err.println("CrearTutor SP failed: " + spEx.getMessage());
            }

            // Fallback: create minimal usuario with correo only via JPA
            Usuario u = new Usuario();
            u.setCorreo(correo);
            u.setNombre((String) body.getOrDefault("nombre", null));
            u.setApellido((String) body.getOrDefault("apellido", null));
            u.setContrasena((String) body.getOrDefault("contrasena", ""));
            u.setRol(Usuario.Rol.tutor);
            u.setEstado(Usuario.Estado.activo);
            u.setFecha_registro(java.time.LocalDate.now());
            usuarioRepo.saveAndFlush(u);

            Tutor t = new Tutor();
            t.setUsuario(u);
            t.setEspecialidad((String) body.getOrDefault("especialidad", null));
            tutorRepo.save(t);
            tutorRepo.flush();
            return ResponseEntity.ok(Map.of("id_tutor", t.getId_tutor()));
        } catch (Exception e) {
            // return class and message to help debugging from the client
            Map<String,Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            err.put("exception", e.getClass().getName());
            return ResponseEntity.status(500).body(err);
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
