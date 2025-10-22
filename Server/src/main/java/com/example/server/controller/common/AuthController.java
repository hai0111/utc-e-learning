package com.example.server.controller.common;

import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.MessageResponse;
import com.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping()
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws ParseException {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws InterruptedException {
        return ResponseEntity.ok(authService.login(loginRequest) == null ? new MessageResponse("Email or password is correct", 1) : authService.login(loginRequest));
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
