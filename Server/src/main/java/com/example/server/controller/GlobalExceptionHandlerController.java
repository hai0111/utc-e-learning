package com.example.server.controller;

import com.example.server.exception.CustomServiceException;
import com.example.server.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomerServiceException(CustomServiceException ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(
                ex.getHttpStatus().value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ApiResponse<Void> errorResponse = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
