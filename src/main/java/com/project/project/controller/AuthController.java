package com.project.project.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Usuario;
import com.project.project.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest, HttpSession session) {
        if (loginRequest == null || loginRequest.getCorreo() == null || loginRequest.getContrasena() == null) {
            return ResponseEntity.badRequest().body("Solicitud de login inválida");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(loginRequest.getCorreo());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // safe compare to avoid NPE
        if (usuario.getContrasena() == null || !java.util.Objects.equals(usuario.getContrasena(), loginRequest.getContrasena())) {
            return ResponseEntity.badRequest().body("Contraseña incorrecta");
        }

        if (usuario.getEstado() != Usuario.Estado.activo) {
            return ResponseEntity.badRequest().body("El usuario está inactivo");
        }

        // 🔹 Redirección según el rol (Usuario.Rol es un enum)
        Usuario.Rol rol = usuario.getRol();
        if (rol == null) {
            return ResponseEntity.badRequest().body("Rol desconocido");
        }

    // set session attributes
    session.setAttribute("usuarioId", usuario.getId_usuario());
    session.setAttribute("usuarioCorreo", usuario.getCorreo());
    session.setAttribute("usuarioRol", rol.name());

    // Also register an Authentication in Spring Security so protected endpoints
    // (e.g. /admin-*.html, /api/admin/**) are accessible after REST login.
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + rol.name().toUpperCase())
    );
    Authentication auth = new UsernamePasswordAuthenticationToken(usuario.getCorreo(), null, authorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
    // store SecurityContext in session so Spring Security recognizes it on subsequent requests
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        switch (rol) {
            case estudiante:
                return ResponseEntity.ok(Map.of(
                        "redirect", "/dashboardEstudiante.html",
                        "rol", "estudiante",
                        "id", usuario.getId_usuario(),
                        "correo", usuario.getCorreo()
                ));
            case tutor:
                return ResponseEntity.ok(Map.of(
                        "redirect", "/dashboardTutor.html",
                        "rol", "tutor",
                        "id", usuario.getId_usuario(),
                        "correo", usuario.getCorreo()
                ));
            case admin:
                return ResponseEntity.ok(Map.of(
                        "redirect", "/dashboardAdmin.html",
                        "rol", "admin",
                        "id", usuario.getId_usuario(),
                        "correo", usuario.getCorreo()
                ));
            default:
                return ResponseEntity.badRequest().body("Rol desconocido");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            // session already invalidated
        }
        return ResponseEntity.ok(Map.of("loggedOut", true));
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(HttpSession session) {
        Object id = session.getAttribute("usuarioId");
        if (id == null) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "id", id,
                "correo", session.getAttribute("usuarioCorreo"),
                "rol", session.getAttribute("usuarioRol")
        ));
    }
}
