package com.project.project.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "Agendamiento")
public class Agendamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_agenda;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @Getter @Setter
    private Estudiante usuario;

    @ManyToOne
    @JoinColumn(name = "id_tutor")
    @Getter @Setter
    private Tutor tutor;

    @Getter @Setter
    private LocalDate fecha;
    @Getter @Setter
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    public enum Tipo { presencial, virtual }
    public enum Estado { confirmado, cancelado, pendiente }

    // Getters y Setters
}
