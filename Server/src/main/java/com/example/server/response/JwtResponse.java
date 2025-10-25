package com.example.server.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {

    private String jwt;

    private String type = "Bearer";

    private String name;

    private String email;

    private List<String> roles;

    private String message;

    private Integer statusCode;

    public JwtResponse(String jwt, String name, String email, List<String> roles, String message, Integer statusCode) {
        this.jwt = jwt;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.message = message;
        this.statusCode = statusCode;
    }

    public JwtResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
