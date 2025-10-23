package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Tutor;
import com.project.project.repository.TutorRepository;

@Service
public class TutorService {
    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    public List<Tutor> listarTodos() {
        return tutorRepository.findAll();
    }

    public Optional<Tutor> buscarPorId(Integer id) {
        return tutorRepository.findById(id);
    }

    public Tutor guardar(Tutor tutor) {
        return tutorRepository.save(tutor);
    }

    public void eliminar(Integer id) {
        tutorRepository.deleteById(id);
    }
}
