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
@Table(name = "Blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Integer id_post;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @Getter @Setter
    private Usuario usuario;

    @Getter @Setter
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter
    private String contenido;

    @Getter @Setter
    private LocalDate fecha_publicacion;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private Estado estado;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    public enum Estado { activo, borrador }

    // Getters y Setters
}
