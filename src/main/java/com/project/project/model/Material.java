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
@Table(name = "Material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_material;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    @Getter @Setter
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Tipo tipo;

    @Getter @Setter
    private String titulo;
    @Getter @Setter
    private String url_archivo;
    @Getter @Setter
    private LocalDate fecha_carga;

    public enum Tipo { video, pdf, recurso }

    // Getters y Setters
}
