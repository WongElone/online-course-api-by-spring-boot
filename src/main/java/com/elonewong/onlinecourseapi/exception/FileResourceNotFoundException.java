package com.elonewong.onlinecourseapi.exception;


public class FileResourceNotFoundException extends RuntimeException {
    public FileResourceNotFoundException(String message) {
        super(message);
    }

    public FileResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
