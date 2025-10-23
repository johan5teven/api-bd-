package com.project.project.controller;

import com.project.project.model.Material;
import com.project.project.service.MaterialService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materiales")
@CrossOrigin(origins = "*")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public List<Material> listar() {
        return materialService.listarTodos();
    }

    @GetMapping("/{id}")
    public Material buscarPorId(@PathVariable Integer id) {
        return materialService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Material crear(@RequestBody Material material) {
        return materialService.guardar(material);
    }

    @PutMapping("/{id}")
    public Material actualizar(@PathVariable Integer id, @RequestBody Material material) {
        material.setId_material(id);
        return materialService.guardar(material);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        materialService.eliminar(id);
    }
}
