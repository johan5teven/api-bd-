package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
}
