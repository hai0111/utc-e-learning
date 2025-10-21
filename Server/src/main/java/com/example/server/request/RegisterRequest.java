package com.example.server.request;

import com.example.server.enums.Role;
import com.example.server.model.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;

    private String email;

    private String password;

    private Role role;

    private Boolean isActive;

    private Users createdBy;

    private Users updatedBy;
}
