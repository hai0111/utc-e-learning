package com.example.server.response;

import com.example.server.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String jwt;

    private UUID id;

    private String code;

    private String name;

    private String email;

    private Role role;
}
