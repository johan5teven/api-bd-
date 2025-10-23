package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
