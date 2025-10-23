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

import com.project.project.model.Tutor;
import com.project.project.service.TutorService;

@RestController
@RequestMapping("/api/tutores")
@CrossOrigin(origins = "*")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<Tutor> listar() {
        return tutorService.listarTodos();
    }

    @GetMapping("/{id}")
    public Tutor buscarPorId(@PathVariable Integer id) {
        return tutorService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Tutor crear(@RequestBody Tutor tutor) {
        return tutorService.guardar(tutor);
    }

    @PutMapping("/{id}")
    public Tutor actualizar(@PathVariable Integer id, @RequestBody Tutor tutor) {
        tutor.setId_tutor(id);
        return tutorService.guardar(tutor);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        tutorService.eliminar(id);
    }
}
