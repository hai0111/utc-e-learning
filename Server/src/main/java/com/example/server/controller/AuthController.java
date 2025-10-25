package com.example.server.controller;

import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.JwtResponse;
import com.example.server.response.MessageResponse;
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
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) throws InterruptedException {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(
                jwtResponse.getStatusCode() != 200 ?
                        new MessageResponse(jwtResponse.getMessage(), jwtResponse.getStatusCode()) : jwtResponse);
    }

    @GetMapping("/logoutAccount")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok((authService.logout(token)));
    }

    @GetMapping("/principal")
    public ResponseEntity<?> getAccountPrincipal(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getPrincipal(token));
    }
}
