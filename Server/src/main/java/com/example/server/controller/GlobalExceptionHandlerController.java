package com.example.server.controller;

import com.example.server.exception.CustomServiceException;
import com.example.server.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<MessageResponse> handleCustomerServiceException(CustomServiceException ex) {
        MessageResponse errorMessage = new MessageResponse(ex.getMessage(), ex.getHttpStatus().value());
        return new ResponseEntity<>(errorMessage, (ex.getHttpStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGeneralException(Exception ex) {
        MessageResponse errorMessage = new MessageResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
