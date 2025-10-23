package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Orden;
import com.project.project.repository.OrdenRepository;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepository;

    public OrdenService(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    public List<Orden> listarTodos() {
        return ordenRepository.findAll();
    }

    public Optional<Orden> buscarPorId(Integer id) {
        return ordenRepository.findById(id);
    }

    public Orden guardar(Orden orden) {
        return ordenRepository.save(orden);
    }

    public void eliminar(Integer id) {
        ordenRepository.deleteById(id);
    }
}
