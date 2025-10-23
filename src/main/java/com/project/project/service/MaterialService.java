package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Material;
import com.project.project.repository.MaterialRepository;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    public Optional<Material> buscarPorId(Integer id) {
        return materialRepository.findById(id);
    }

    public Material guardar(Material material) {
        return materialRepository.save(material);
    }

    public void eliminar(Integer id) {
        materialRepository.deleteById(id);
    }
}
