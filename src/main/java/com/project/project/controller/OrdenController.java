package com.project.project.controller;

import com.project.project.model.Orden;
import com.project.project.service.OrdenService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public List<Orden> listar() {
        return ordenService.listarTodos();
    }

    @GetMapping("/{id}")
    public Orden buscarPorId(@PathVariable Integer id) {
        return ordenService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Orden crear(@RequestBody Orden orden) {
        return ordenService.guardar(orden);
    }

    @PutMapping("/{id}")
    public Orden actualizar(@PathVariable Integer id, @RequestBody Orden orden) {
        orden.setId_orden(id);
        return ordenService.guardar(orden);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        ordenService.eliminar(id);
    }
}
