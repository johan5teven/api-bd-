package com.project.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiante")
@CrossOrigin(origins = "*")
public class EstudianteDashboardController {

    @GetMapping("/inscripciones")
    public ResponseEntity<List<Map<String, Object>>> getInscripciones() {
        // Datos de ejemplo para el dashboard
        List<Map<String, Object>> inscripciones = List.of(
            Map.of("id", 1, "curso", "Java Básico", "fecha", LocalDateTime.now().minusDays(10)),
            Map.of("id", 2, "curso", "Spring Boot", "fecha", LocalDateTime.now().minusDays(5))
        );
        return ResponseEntity.ok(inscripciones);
    }

    @GetMapping("/materiales")
    public ResponseEntity<List<Map<String, Object>>> getMateriales() {
        List<Map<String, Object>> materiales = List.of(
            Map.of("id", 1, "titulo", "Manual Java", "tipo", "PDF"),
            Map.of("id", 2, "titulo", "Video Tutorial Spring", "tipo", "MP4"),
            Map.of("id", 3, "titulo", "Ejercicios Prácticos", "tipo", "ZIP")
        );
        return ResponseEntity.ok(materiales);
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<Map<String, Object>>> getPagos() {
        List<Map<String, Object>> pagos = List.of(
            Map.of("id", 1, "monto", 150.0, "fecha", LocalDateTime.now().minusDays(15), "concepto", "Inscripción Java Básico"),
            Map.of("id", 2, "monto", 200.0, "fecha", LocalDateTime.now().minusDays(8), "concepto", "Inscripción Spring Boot")
        );
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/mis-tutores")
    public ResponseEntity<List<Map<String, Object>>> getMisTutores() {
        List<Map<String, Object>> tutores = List.of(
            Map.of("id", 1, "nombre", "Prof. García", "especialidad", "Java", "email", "garcia@ejemplo.com"),
            Map.of("id", 2, "nombre", "Prof. Martínez", "especialidad", "Spring Framework", "email", "martinez@ejemplo.com")
        );
        return ResponseEntity.ok(tutores);
    }

    @GetMapping("/actividad-reciente")
    public ResponseEntity<List<Map<String, Object>>> getActividadReciente() {
        List<Map<String, Object>> actividad = List.of(
            Map.of("fecha", LocalDateTime.now().minusHours(2), "descripcion", "Descargó material: Manual Java"),
            Map.of("fecha", LocalDateTime.now().minusHours(6), "descripcion", "Completó lección: Introducción a Spring"),
            Map.of("fecha", LocalDateTime.now().minusDays(1), "descripcion", "Se inscribió al curso: Spring Boot Avanzado"),
            Map.of("fecha", LocalDateTime.now().minusDays(2), "descripcion", "Realizó pago de $200.00"),
            Map.of("fecha", LocalDateTime.now().minusDays(3), "descripcion", "Actualizó información de perfil")
        );
        return ResponseEntity.ok(actividad);
    }
}