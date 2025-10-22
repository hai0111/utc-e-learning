package com.example.server.service;

import com.example.server.model.Users;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.JwtResponse;
import com.example.server.response.MessageResponse;

import java.text.ParseException;

public interface AuthService {

    MessageResponse register(RegisterRequest registerRequest) throws ParseException;

    JwtResponse login(LoginRequest registerRequest) throws InterruptedException;

    MessageResponse logout(String token);

    Users getPrincipal(String token);
}
