package com.example.server.exception;

import org.springframework.http.HttpStatus;

public class CustomServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
