package com.project.project.controller;

import com.project.project.model.Estudiante;
import com.project.project.service.EstudianteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
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
}
