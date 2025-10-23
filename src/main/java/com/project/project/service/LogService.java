package com.project.project.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.project.project.exception.LogStorageException;
import com.project.project.model.LogEventoMongo;
import com.project.project.repository.LogMongoRepository;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private final LogMongoRepository logRepo;

    public LogService(LogMongoRepository logRepo) {
        this.logRepo = logRepo;
    }

    /**
     * Save a log event into MongoDB. Adds timestamp if missing and logs failures.
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
}
