package com.project.project.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=testdb"
})
public class MongoIndexConfigTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testTTLIndexesCreated() {
        String[] collections = {"auth_logs", "activity_logs", "error_logs", "event_logs"};
        long expectedTTL = TimeUnit.DAYS.toSeconds(90);
        
        for (String collection : collections) {
            List<IndexInfo> indexes = mongoTemplate.indexOps(collection).getIndexInfo();
            
            boolean hasTTLIndex = indexes.stream()
                .anyMatch(index -> 
                    index.getExpireAfter() != null && 
                    index.getExpireAfter().isPresent() &&
                    index.getExpireAfter().get().getSeconds() == expectedTTL &&
                    index.getIndexFields().stream().anyMatch(field -> "fecha".equals(field.getKey()))
                );
            
            assertTrue(hasTTLIndex, 
                "Collection " + collection + " should have TTL index on 'fecha' field with 90 days expiration");
        }
    }
}