package com.project.project.controller;

import com.project.project.model.Estudiante;
import com.project.project.model.Inscripcion;
import com.project.project.model.Material;
import com.project.project.model.Pago;
import com.project.project.model.Tutor;
import com.project.project.service.EstudianteService;
import com.project.project.service.InscripcionService;
import com.project.project.service.MaterialService;
import com.project.project.service.PagoService;
import com.project.project.service.TutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final InscripcionService inscripcionService;
    private final MaterialService materialService;
    private final PagoService pagoService;
    private final TutorService tutorService;

    public EstudianteController(EstudianteService estudianteService, 
                               InscripcionService inscripcionService,
                               MaterialService materialService,
                               PagoService pagoService,
                               TutorService tutorService) {
        this.estudianteService = estudianteService;
        this.inscripcionService = inscripcionService;
        this.materialService = materialService;
        this.pagoService = pagoService;
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<Estudiante> listar() {
        return estudianteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Estudiante buscarPorId(@PathVariable Integer id) {
        return estudianteService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Estudiante crear(@RequestBody Estudiante estudiante) {
        return estudianteService.guardar(estudiante);
    }

    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Integer id, @RequestBody Estudiante estudiante) {
        estudiante.setId_estudiante(id);
        return estudianteService.guardar(estudiante);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        estudianteService.eliminar(id);
    }

    // Endpoints específicos para el dashboard del estudiante
    @GetMapping("/{id}/inscripciones")
    public ResponseEntity<List<Inscripcion>> getInscripciones(@PathVariable Integer id) {
        try {
            List<Inscripcion> inscripciones = inscripcionService.listarTodos();
            return ResponseEntity.ok(inscripciones);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/materiales")
    public ResponseEntity<List<Material>> getMateriales(@PathVariable Integer id) {
        try {
            List<Material> materiales = materialService.listarTodos();
            return ResponseEntity.ok(materiales);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<Pago>> getPagos(@PathVariable Integer id) {
        try {
            List<Pago> pagos = pagoService.listarTodos();
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/tutores")
    public ResponseEntity<List<Tutor>> getTutores(@PathVariable Integer id) {
        try {
            List<Tutor> tutores = tutorService.listarTodos();
            return ResponseEntity.ok(tutores);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}/actividad-reciente")
    public ResponseEntity<List<Map<String, Object>>> getActividadReciente(@PathVariable Integer id) {
        try {
            List<Map<String, Object>> actividad = List.of(
                Map.of("fecha", LocalDateTime.now().minusDays(1), "descripcion", "Se inscribió a un nuevo curso"),
                Map.of("fecha", LocalDateTime.now().minusDays(2), "descripcion", "Descargó material de estudio"),
                Map.of("fecha", LocalDateTime.now().minusDays(3), "descripcion", "Realizó pago de inscripción"),
                Map.of("fecha", LocalDateTime.now().minusDays(5), "descripcion", "Completó perfil de estudiante")
            );
            return ResponseEntity.ok(actividad);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
