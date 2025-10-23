package com.project.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Agendamiento;
import com.project.project.service.AgendamientoService;

@RestController
@RequestMapping("/api/agendamientos")
@CrossOrigin(origins = "*")
public class AgendamientoController {

    private final AgendamientoService agendamientoService;

    public AgendamientoController(AgendamientoService agendamientoService) {
        this.agendamientoService = agendamientoService;
    }

    @GetMapping
    public List<Agendamiento> listar() {
        return agendamientoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Agendamiento buscarPorId(@PathVariable Integer id) {
        return agendamientoService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Agendamiento crear(@RequestBody Agendamiento agendamiento) {
        return agendamientoService.guardar(agendamiento);
    }

    @PutMapping("/{id}")
    public Agendamiento actualizar(@PathVariable Integer id, @RequestBody Agendamiento agendamiento) {
        agendamiento.setId_agenda(id);
        return agendamientoService.guardar(agendamiento);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        agendamientoService.eliminar(id);
    }
}
