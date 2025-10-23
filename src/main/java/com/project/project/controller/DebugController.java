package com.project.project.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Usuario;
import com.project.project.repository.EstudianteRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;

    public DebugController(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public static record UsuarioDto(Integer id, String nombre, String apellido, String correo, LocalDate fechaRegistro) {}
    public static record EstudianteDto(Integer idEstudiante, String nivel, String intereses) {}

    @GetMapping("/users")
    public List<UsuarioDto> listUsers(@RequestParam(required = false) String correo) {
        List<Usuario> users;
        if (correo != null && !correo.isEmpty()) {
            users = usuarioRepository.findByCorreo(correo).map(List::of).orElse(List.of());
        } else {
            users = usuarioRepository.findAll();
        }
        return users.stream()
                .map(u -> new UsuarioDto(u.getId_usuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getFecha_registro()))
                .collect(Collectors.toList());
    }

    @GetMapping("/estudiantes")
    public List<EstudianteDto> listEstudiantes() {
        return estudianteRepository.findAll().stream()
                .map(e -> new EstudianteDto(e.getId_estudiante(), e.getNivel(), e.getIntereses()))
                .collect(Collectors.toList());
    }
}
