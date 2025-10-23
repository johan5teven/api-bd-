package com.project.project.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Estudiante;
import com.project.project.model.Usuario;
import com.project.project.repository.EstudianteRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
public class RegistrationController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;

    public RegistrationController(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public static class RegistrationRequest {
        public UsuarioDTO usuario;
        public EstudianteDTO estudiante;

        public static class UsuarioDTO {
            public String nombre;
            public String apellido;
            public String correo;
            public String contrasena;
            public String rol;
            // opcional: puede ser "yyyy-MM-dd" o un timestamp en ms
            public String fecha_registro;
        }

        public static class EstudianteDTO {
            public String nivel;
            public String intereses;
        }
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest req) {
        if (req == null || req.usuario == null) {
            return ResponseEntity.badRequest().body("Invalid payload");
        }
        // validar correo duplicado
        if (req.usuario.correo != null && usuarioRepository.existsByCorreo(req.usuario.correo)) {
            return ResponseEntity.status(409).body("Correo already exists");
        }

        // create Usuario
        Usuario u = new Usuario();
        u.setNombre(req.usuario.nombre);
        u.setApellido(req.usuario.apellido);
        u.setCorreo(req.usuario.correo);
        u.setContrasena(req.usuario.contrasena);
        try {
            u.setRol(Usuario.Rol.valueOf(req.usuario.rol));
        } catch (Exception ex) {
            u.setRol(Usuario.Rol.estudiante);
        }

        // parse optional fecha_registro (accepts yyyy-MM-dd or epoch millis)
        LocalDate fecha = LocalDate.now();
        if (req.usuario.fecha_registro != null && !req.usuario.fecha_registro.isBlank()) {
            String s = req.usuario.fecha_registro.trim();
            try {
                // si es dígitos, tratar como epoch millis
                if (Pattern.matches("^\\d+$", s)) {
                    long ms = Long.parseLong(s);
                    fecha = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    // try parse as LocalDate first (yyyy-MM-dd)
                    try {
                        fecha = LocalDate.parse(s);
                    } catch (DateTimeParseException inner) {
                        // try full ISO instant / date-time with fractionals (e.g. 2025-10-21T13:59:45.6483717)
                        try {
                            Instant inst = Instant.parse(s);
                            fecha = inst.atZone(ZoneId.systemDefault()).toLocalDate();
                        } catch (DateTimeParseException ex2) {
                            // As a more lenient fallback, try to parse up to seconds (replace space with T if needed)
                            String normalized = s.contains(" ") ? s.replace(' ', 'T') : s;
                            try {
                                Instant inst2 = Instant.parse(normalized);
                                fecha = inst2.atZone(ZoneId.systemDefault()).toLocalDate();
                            } catch (DateTimeParseException ex3) {
                                // última alternativa: intentar parse con LocalDate.parse truncando la parte de tiempo
                                int tIndex = normalized.indexOf('T');
                                if (tIndex > 0) {
                                    try {
                                        fecha = LocalDate.parse(normalized.substring(0, tIndex));
                                    } catch (DateTimeParseException ex4) {
                                        fecha = LocalDate.now();
                                    }
                                } else {
                                    fecha = LocalDate.now();
                                }
                            }
                        }
                    }
                }
            } catch (DateTimeParseException | NumberFormatException e) {
                // si falla el parseo, usar la fecha actual
                fecha = LocalDate.now();
            }
        }

        u.setFecha_registro(fecha);
        u.setEstado(Usuario.Estado.activo);

    Usuario saved = usuarioRepository.save(u);

    // debug: print saved id to console
    System.out.println("[DEBUG] Usuario saved id=" + saved.getId_usuario());

    // create Estudiante and associate to saved usuario. When Estudiante uses @MapsId, do NOT set id manually.
    Estudiante e = new Estudiante();
    e.setNivel(req.estudiante != null && req.estudiante.nivel != null ? req.estudiante.nivel : "principiante");
    e.setIntereses(req.estudiante != null ? req.estudiante.intereses : "");
    e.setUsuario(saved);
    Estudiante savedE = estudianteRepository.save(e);

    System.out.println("[DEBUG] Estudiante saved id=" + savedE.getId_estudiante());

    return ResponseEntity.ok(saved);
    }
}
