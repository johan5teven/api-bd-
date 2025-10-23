package com.project.project.exception;

/**
 * Custom runtime exception for errors when storing logs.
 */
public class LogStorageException extends RuntimeException {

    public LogStorageException() {
        super();
    }

    public LogStorageException(String message) {
        super(message);
    }

    public LogStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogStorageException(Throwable cause) {
        super(cause);
    }
}
