package com.project.project.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "encrypted_users")
public class EncryptedUser {
    @Id
    private String id;
    private Instant createdAt = Instant.now();
    private String iv; // base64
    private String ciphertext; // base64
    private String algorithm = "AES/GCM/NoPadding";

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    public String getCiphertext() { return ciphertext; }
    public void setCiphertext(String ciphertext) { this.ciphertext = ciphertext; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
