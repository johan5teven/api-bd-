package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {
}
