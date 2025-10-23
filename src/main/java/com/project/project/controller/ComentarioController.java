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

import com.project.project.model.Comentario;
import com.project.project.service.ComentarioService;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<Comentario> listar() {
        return comentarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Comentario buscarPorId(@PathVariable Integer id) {
        return comentarioService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Comentario crear(@RequestBody Comentario comentario) {
        return comentarioService.guardar(comentario);
    }

    @PutMapping("/{id}")
    public Comentario actualizar(@PathVariable Integer id, @RequestBody Comentario comentario) {
        comentario.setId_comentario(id);
        return comentarioService.guardar(comentario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        comentarioService.eliminar(id);
    }
}
