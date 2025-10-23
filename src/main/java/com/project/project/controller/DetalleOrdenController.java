package com.project.project.controller;

import com.project.project.model.DetalleOrden;
import com.project.project.service.DetalleOrdenService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-orden")
@CrossOrigin(origins = "*")
public class DetalleOrdenController {

    private final DetalleOrdenService detalleOrdenService;

    public DetalleOrdenController(DetalleOrdenService detalleOrdenService) {
        this.detalleOrdenService = detalleOrdenService;
    }

    @GetMapping
    public List<DetalleOrden> listar() {
        return detalleOrdenService.listarTodos();
    }

    @GetMapping("/{id}")
    public DetalleOrden buscarPorId(@PathVariable Integer id) {
        return detalleOrdenService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public DetalleOrden crear(@RequestBody DetalleOrden detalle) {
        return detalleOrdenService.guardar(detalle);
    }

    @PutMapping("/{id}")
    public DetalleOrden actualizar(@PathVariable Integer id, @RequestBody DetalleOrden detalle) {
        detalle.setId_detalle(id);
        return detalleOrdenService.guardar(detalle);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        detalleOrdenService.eliminar(id);
    }
}
