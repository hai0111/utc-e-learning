package com.example.server.service;

import com.example.server.model.Users;
import com.example.server.request.ChangePasswordRequest;
import com.example.server.request.ForgotPasswordRequest;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.request.VerifyCodeAndResetPasswordRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.JwtResponse;
import com.example.server.response.UsersDetailsResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;

public interface AuthService {

    ApiResponse<Users> register(RegisterRequest registerRequest) throws ParseException;

    ApiResponse<JwtResponse> login(LoginRequest loginRequest);

    ApiResponse<UsersDetailsResponse> getPrincipalFromContext();

    ApiResponse<Users> changePassword(ChangePasswordRequest changePasswordRequest);

    ApiResponse<Void> forgotPassword(ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest httpServletRequest);

    ApiResponse<Void> verifyCodeAndSetNewPassword(VerifyCodeAndResetPasswordRequest request, HttpServletRequest httpServletRequest);
}
