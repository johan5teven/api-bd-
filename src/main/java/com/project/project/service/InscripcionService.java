package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Inscripcion;
import com.project.project.repository.InscripcionRepository;

@Service
public class InscripcionService {
    private final InscripcionRepository inscripcionRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<Inscripcion> listarTodos() {
        return inscripcionRepository.findAll();
    }

    public Optional<Inscripcion> buscarPorId(Integer id) {
        return inscripcionRepository.findById(id);
    }

    public Inscripcion guardar(Inscripcion inscripcion) {
        return inscripcionRepository.save(inscripcion);
    }

    public void eliminar(Integer id) {
        inscripcionRepository.deleteById(id);
    }
}
