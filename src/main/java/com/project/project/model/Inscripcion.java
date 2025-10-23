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
@Table(name = "Inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_inscripcion;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    @Getter @Setter
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    @Getter @Setter
    private Curso curso;

    @Getter @Setter
    private LocalDate fecha_inscripcion;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    public enum Estado { activo, cancelado, finalizado }

    // Getters y Setters
}
