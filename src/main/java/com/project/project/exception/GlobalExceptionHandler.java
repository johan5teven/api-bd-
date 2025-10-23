package com.project.project.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.project.model.LogEventoMongo;
import com.project.project.service.LogMongoService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final LogMongoService logService;

    public GlobalExceptionHandler(LogMongoService logService) {
        this.logService = logService;
    }

    @ExceptionHandler(LogStorageException.class)
    @ResponseBody
    public ResponseEntity<Object> handleLogStorage(LogStorageException ex, HttpServletRequest request) {
        logger.error("Log storage exception: {}", ex.getMessage(), ex);

        // Try to persist the error info as a log; swallow any failure to avoid recursion
        try {
            LogEventoMongo evento = new LogEventoMongo();
            evento.setTabla("application");
            evento.setOperacion("log_storage_exception");
            evento.setDescripcion(ex.getMessage());
            evento.setUsuario(request.getRemoteUser());
            evento.setIp(request.getRemoteAddr());
            evento.setFecha(LocalDateTime.now());

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            evento.setDetalle(sw.toString());

            // persistence may fail again, so catch inside LogService or here
            try {
                // use registrarEvento convenience method
                logService.registrarEvento(evento.getTabla(), evento.getOperacion(), evento.getDescripcion(), evento.getUsuario(), evento.getIp(), evento.getDetalle());
            } catch (Exception e) {
                logger.error("Failed to persist log for LogStorageException: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error while preparing log for LogStorageException: {}", e.getMessage(), e);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "log_storage_error");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleGeneric(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);

        // Persist the exception details to MongoDB (best-effort)
        try {
            LogEventoMongo evento = new LogEventoMongo();
            evento.setTabla("application");
            evento.setOperacion("exception");
            evento.setDescripcion(ex.getMessage());
            evento.setUsuario(request.getRemoteUser());
            evento.setIp(request.getRemoteAddr());
            evento.setFecha(LocalDateTime.now());

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            evento.setDetalle(sw.toString());

            try {
                logService.registrarEvento(evento.getTabla(), evento.getOperacion(), evento.getDescripcion(), evento.getUsuario(), evento.getIp(), evento.getDetalle());
            } catch (Exception e) {
                logger.error("Failed to persist log for exception: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error while preparing log for exception: {}", e.getMessage(), e);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "internal_error");
        body.put("message", "Ocurrió un error interno");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
