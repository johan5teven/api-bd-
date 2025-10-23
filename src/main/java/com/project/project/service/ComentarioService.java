package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Comentario;
import com.project.project.repository.ComentarioRepository;

@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public List<Comentario> listarTodos() {
        return comentarioRepository.findAll();
    }

    public Optional<Comentario> buscarPorId(Integer id) {
        return comentarioRepository.findById(id);
    }

    public Comentario guardar(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    public void eliminar(Integer id) {
        comentarioRepository.deleteById(id);
    }
}
