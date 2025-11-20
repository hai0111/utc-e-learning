package com.example.server.controller;

import com.example.server.request.ChangePasswordRequest;
import com.example.server.request.ForgotPasswordRequest;
import com.example.server.request.VerifyCodeAndResetPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping()
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest) throws ParseException {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authService.changePassword(changePasswordRequest));
    }

    @GetMapping("/principal")
    public ResponseEntity<?> getAccountPrincipal() {
        return ResponseEntity.ok(authService.getPrincipalFromContext());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest,
                                            HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequest, httpServletRequest));
    }

    @PostMapping("/verify-code-and-reset-password")
    public ResponseEntity<?> verifyCodeAndResetPassword(@RequestBody @Valid VerifyCodeAndResetPasswordRequest request,
                                                        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.verifyCodeAndSetNewPassword(request, httpServletRequest));
    }
}
