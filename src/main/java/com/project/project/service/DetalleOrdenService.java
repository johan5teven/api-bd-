package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.DetalleOrden;
import com.project.project.repository.DetalleOrdenRepository;

@Service
public class DetalleOrdenService {
    private final DetalleOrdenRepository detalleOrdenRepository;

    public DetalleOrdenService(DetalleOrdenRepository detalleOrdenRepository) {
        this.detalleOrdenRepository = detalleOrdenRepository;
    }

    public List<DetalleOrden> listarTodos() {
        return detalleOrdenRepository.findAll();
    }

    public Optional<DetalleOrden> buscarPorId(Integer id) {
        return detalleOrdenRepository.findById(id);
    }

    public DetalleOrden guardar(DetalleOrden detalle) {
        return detalleOrdenRepository.save(detalle);
    }

    public void eliminar(Integer id) {
        detalleOrdenRepository.deleteById(id);
    }
}
