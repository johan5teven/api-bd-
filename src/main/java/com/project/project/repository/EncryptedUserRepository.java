package com.project.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.project.model.EncryptedUser;

public interface EncryptedUserRepository extends MongoRepository<EncryptedUser, String> {

}
