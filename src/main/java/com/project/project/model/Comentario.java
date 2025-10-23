package com.project.project.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
@Table(name = "Comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_comentario;

    @ManyToOne
    @JoinColumn(name = "id_post")
    @Getter @Setter
    private Blog post;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @Getter @Setter
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter
    private String contenido;

    @Getter @Setter
    private LocalDate fecha;

    // Getters y Setters
}
