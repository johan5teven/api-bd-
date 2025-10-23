package com.project.project.controller;

import com.project.project.model.Inscripcion;
import com.project.project.service.InscripcionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "*")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public List<Inscripcion> listar() {
        return inscripcionService.listarTodos();
    }

    @GetMapping("/{id}")
    public Inscripcion buscarPorId(@PathVariable Integer id) {
        return inscripcionService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Inscripcion crear(@RequestBody Inscripcion inscripcion) {
        return inscripcionService.guardar(inscripcion);
    }

    @PutMapping("/{id}")
    public Inscripcion actualizar(@PathVariable Integer id, @RequestBody Inscripcion inscripcion) {
        inscripcion.setId_inscripcion(id);
        return inscripcionService.guardar(inscripcion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        inscripcionService.eliminar(id);
    }
}
