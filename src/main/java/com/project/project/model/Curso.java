package com.project.project.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_curso;

    @Getter @Setter
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Modalidad modalidad;

    @Getter @Setter
    private LocalDate fecha_inicio;
    @Getter @Setter
    private LocalDate fecha_fin;
    @Getter @Setter
    private Double precio;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "id_tutor")
    private Tutor tutor;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Material> materiales;

    public enum Modalidad { presencial, virtual }
    public enum Estado { activo, inactivo }

    // Getters y Setters
}
