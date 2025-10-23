package com.project.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_orden;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @Getter @Setter
    private Usuario usuario;

    @Getter @Setter
    private LocalDate fecha_orden;
    @Getter @Setter
    private Double total;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles;

    public enum Estado { pendiente, pagado, cancelado }

    // Getters y Setters
}
