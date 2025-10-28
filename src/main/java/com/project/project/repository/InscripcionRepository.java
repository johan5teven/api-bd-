package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.project.model.Inscripcion;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
	@Query("SELECT CASE WHEN COUNT(i)>0 THEN true ELSE false END FROM Inscripcion i WHERE i.estudiante.id_estudiante = :idEstudiante AND i.curso.id_curso = :idCurso")
	boolean existsByEstudianteIdAndCursoId(@Param("idEstudiante") Integer idEstudiante, @Param("idCurso") Integer idCurso);
}
