package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
}
