package com.project.project.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.project.project.exception.LogStorageException;
import com.project.project.model.LogEventoMongo;

@Service
public class LogMongoService {

    private static final Logger logger = LoggerFactory.getLogger(LogMongoService.class);

    private final MongoTemplate mongoTemplate;

    public LogMongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Save a LogEventoMongo, ensuring timestamp and consistent error handling.
     * Routes to appropriate collection based on event type.
     */
    public LogEventoMongo save(LogEventoMongo evento) {
        try {
            if (evento.getFecha() == null) {
                evento.setFecha(LocalDateTime.now());
            }
            
            String collection = mapCollection(evento.getTabla(), evento.getOperacion());
            LogEventoMongo saved = mongoTemplate.save(evento, collection);
            logger.debug("Log saved: id={} tabla={} operacion={} collection={}", saved.getId(), saved.getTabla(), saved.getOperacion(), collection);
            return saved;
        } catch (Exception ex) {
            logger.error("Failed to save log evento to MongoDB: {}", ex.getMessage(), ex);
            throw new LogStorageException("Error al almacenar el log en MongoDB", ex);
        }
    }

    /**
     * Convenience method used by controllers: build the document and persist it.
     */
    public void registrarEvento(String tabla, String operacion, String descripcion, String usuario, String ip, Object detalle) {
        LogEventoMongo log = new LogEventoMongo();
        log.setTabla(tabla);
        log.setOperacion(operacion);
        log.setDescripcion(descripcion);
        log.setUsuario(usuario);
        log.setIp(ip);
        log.setDetalle(detalle);
        // save(...) handles fecha and exceptions
        save(log);
    }

    /**
     * Convenience overloads: allow callers to pass fewer args.
     */
    public void registrarEvento(String tabla, String operacion, String descripcion) {
        registrarEvento(tabla, operacion, descripcion, null, null, null);
    }

    public void registrarEvento(String tabla, String operacion, String descripcion, String usuario) {
        registrarEvento(tabla, operacion, descripcion, usuario, null, null);
    }

    /**
     * Maps event type to appropriate collection for routing logs
     */
    private String mapCollection(String tabla, String operacion) {
        if (tabla == null) tabla = "";
        if (operacion == null) operacion = "";
        
        // Eventos de autenticación
        if (tabla.equalsIgnoreCase("usuario") && (operacion.contains("login") || operacion.contains("logout") || operacion.contains("auth"))) {
            return "auth_logs";
        }
        
        // Errores y excepciones
        if (operacion.contains("error") || operacion.contains("exception") || operacion.contains("fail")) {
            return "error_logs";
        }
        
        // Actividades de negocio (compras, creación de cursos, etc.)
        if (tabla.matches("(?i)(curso|orden|pago|inscripcion|material)") || 
            operacion.matches("(?i)(crear|comprar|pagar|inscribir|curso_|orden_)")) {
            return "activity_logs";
        }
        
        // Por defecto, eventos generales
        return "event_logs";
    }
}
