package com.project.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Tutor")
public class Tutor {

    @Id
    @Getter @Setter
    private Integer id_tutor;
    @Getter @Setter
    private String especialidad;
    @Getter @Setter
    private Integer experiencia;

    @Column(columnDefinition = "TEXT")
    @Getter @Setter
    private String biografia;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_tutor")
    @Getter @Setter
    private Usuario usuario;

}
