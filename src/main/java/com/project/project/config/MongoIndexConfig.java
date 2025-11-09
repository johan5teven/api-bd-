package com.project.project.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MongoIndexConfig implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear índices TTL para las colecciones de logs (90 días de retención)
        long ttlSeconds = TimeUnit.DAYS.toSeconds(90);
        
        // Índice TTL para auth_logs
        createTTLIndex("auth_logs", ttlSeconds);
        
        // Índice TTL para activity_logs  
        createTTLIndex("activity_logs", ttlSeconds);
        
        // Índice TTL para error_logs
        createTTLIndex("error_logs", ttlSeconds);
        
        // Índice TTL para event_logs
        createTTLIndex("event_logs", ttlSeconds);
        
        System.out.println("[INFO] Índices TTL creados para colecciones de logs (90 días de retención)");
    }
    
    private void createTTLIndex(String collectionName, long ttlSeconds) {
        try {
            Index ttlIndex = new Index()
                .on("fecha", org.springframework.data.domain.Sort.Direction.ASC)
                .expire(ttlSeconds, TimeUnit.SECONDS);
            
            // Crear el índice usando el método actual
            mongoTemplate.indexOps(collectionName).ensureIndex(ttlIndex);
            System.out.println("[INFO] Índice TTL creado en colección " + collectionName);
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo crear índice TTL para " + collectionName + ": " + e.getMessage());
        }
    }
}