package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
	@org.springframework.data.jpa.repository.Query("select m from Material m where m.curso.id_curso = :cursoId")
	java.util.List<com.project.project.model.Material> findByCursoId(@org.springframework.data.repository.query.Param("cursoId") Integer cursoId);
}
