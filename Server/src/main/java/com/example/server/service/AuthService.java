package com.example.server.service;

import com.example.server.model.Users;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.JwtResponse;

import java.text.ParseException;

public interface AuthService {

    ApiResponse<Users> register(RegisterRequest registerRequest) throws ParseException;

    ApiResponse<JwtResponse> login(LoginRequest loginRequest);

    Users getPrincipal(String token);
}
