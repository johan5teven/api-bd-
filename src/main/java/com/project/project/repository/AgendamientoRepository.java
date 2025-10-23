package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Agendamiento;

@Repository
public interface AgendamientoRepository extends JpaRepository<Agendamiento, Integer> {
}
