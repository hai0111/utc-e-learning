package com.example.server.controller;

import com.example.server.exception.CustomServiceException;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.JwtResponse;
import com.example.server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Objects;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping()
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) throws ParseException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new CustomServiceException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST).getHttpStatus());
        }
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) throws InterruptedException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new CustomServiceException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), HttpStatus.BAD_REQUEST).getHttpStatus());
        }
        ApiResponse<JwtResponse> response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/principal")
    public ResponseEntity<?> getAccountPrincipal(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getPrincipal(token));
    }
}
