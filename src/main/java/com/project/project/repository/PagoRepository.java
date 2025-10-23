package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
