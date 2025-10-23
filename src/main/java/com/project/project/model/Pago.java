package com.project.project.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_pago;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    @Getter @Setter
    private Orden orden;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Metodo metodo;
    @Getter @Setter
    private LocalDate fecha_pago;
    @Getter @Setter
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    public enum Metodo { tarjeta, transferencia, otro }
    public enum Estado { exitoso, fallido, pendiente }

    // Getters y Setters
}
