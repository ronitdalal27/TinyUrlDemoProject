package com.example.tinyurl.exception;

public class UrlAlreadyExistsException extends RuntimeException {

    public UrlAlreadyExistsException(String message) {
        super(message);
    }
}
