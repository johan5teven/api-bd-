package com.project.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Detalle_Orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_detalle;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    @Getter @Setter
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    @Getter @Setter
    private Producto producto;

    @Getter @Setter
    private Integer cantidad;
    @Getter @Setter
    private Double precio_unitario;

    // Getters y Setters
}
