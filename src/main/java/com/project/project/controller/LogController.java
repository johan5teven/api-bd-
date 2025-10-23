package com.project.project.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.LogEventoMongo;
import com.project.project.service.LogMongoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogMongoService logService;

    public LogController(LogMongoService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<?> saveLog(@RequestBody(required = false) Map<String, Object> body, HttpServletRequest req, @org.springframework.web.bind.annotation.RequestBody(required = false) String raw) {
        try {
            // if body is null (e.g. Content-Type: text/plain from navigator.sendBeacon), try to parse raw JSON
            if (body == null || body.isEmpty()) {
                try {
                    if (raw != null && raw.trim().startsWith("{")) {
                        body = new com.fasterxml.jackson.databind.ObjectMapper().readValue(raw, java.util.Map.class);
                    } else {
                        body = java.util.Collections.singletonMap("descripcion", raw);
                    }
                } catch (Exception ex) {
                    body = java.util.Collections.singletonMap("descripcion", raw != null ? raw : "");
                }
            }
            LogEventoMongo e = new LogEventoMongo();
            e.setTabla((String) body.getOrDefault("tabla", "ui"));
            e.setOperacion((String) body.getOrDefault("operacion", "click"));
            e.setDescripcion((String) body.getOrDefault("descripcion", null));
            e.setUsuario((String) body.getOrDefault("usuario", null));
            e.setIp(req.getRemoteAddr());
            e.setDetalle(body.getOrDefault("detalle", null));
            var saved = logService.save(e);
            return ResponseEntity.ok(Map.of("ok", true, "id", saved.getId()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
