package com.project.project.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "logs_eventos")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogEventoMongo {

    @Id
    private String id;

    private String tabla;
    private String operacion;
    private String descripcion;
    private String usuario;
    private String ip;
    private LocalDateTime fecha;
    private Object detalle;
}
