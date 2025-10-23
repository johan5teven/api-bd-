package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Agendamiento;
import com.project.project.repository.AgendamientoRepository;

@Service
public class AgendamientoService {
    private final AgendamientoRepository agendamientoRepository;

    public AgendamientoService(AgendamientoRepository agendamientoRepository) {
        this.agendamientoRepository = agendamientoRepository;
    }

    public List<Agendamiento> listarTodos() {
        return agendamientoRepository.findAll();
    }

    public Optional<Agendamiento> buscarPorId(Integer id) {
        return agendamientoRepository.findById(id);
    }

    public Agendamiento guardar(Agendamiento agendamiento) {
        return agendamientoRepository.save(agendamiento);
    }

    public void eliminar(Integer id) {
        agendamientoRepository.deleteById(id);
    }
}
