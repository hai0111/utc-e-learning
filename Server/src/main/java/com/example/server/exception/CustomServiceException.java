package com.example.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
