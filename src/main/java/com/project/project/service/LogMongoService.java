package com.project.project.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.project.project.exception.LogStorageException;
import com.project.project.model.LogEventoMongo;
import com.project.project.repository.LogMongoRepository;

@Service
public class LogMongoService {

    private static final Logger logger = LoggerFactory.getLogger(LogMongoService.class);

    private final LogMongoRepository logRepo;

    public LogMongoService(LogMongoRepository logRepo) {
        this.logRepo = logRepo;
    }

    /**
     * Save a LogEventoMongo, ensuring timestamp and consistent error handling.
     */
    public LogEventoMongo save(LogEventoMongo evento) {
        try {
            if (evento.getFecha() == null) {
                evento.setFecha(LocalDateTime.now());
            }
            LogEventoMongo saved = logRepo.save(evento);
            logger.debug("Log saved: id={} tabla={} operacion={}", saved.getId(), saved.getTabla(), saved.getOperacion());
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
}
