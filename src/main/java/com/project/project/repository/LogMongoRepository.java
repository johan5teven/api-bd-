package com.project.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.project.model.LogEventoMongo;

/**
 * MongoDB repository for persisting LogEventoMongo documents.
 */
public interface LogMongoRepository extends MongoRepository<LogEventoMongo, String> {

}
