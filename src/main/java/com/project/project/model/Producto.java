package com.project.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_producto;

    @Getter @Setter
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter
    private String descripcion;

    @Getter @Setter
    private Double precio;
    @Getter @Setter
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    public enum Estado { disponible, agotado }

    // Getters y Setters
}
