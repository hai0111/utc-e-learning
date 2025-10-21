package com.example.server.model;

import com.example.server.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Email")
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "Role")
    private Role role;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "CreatedBy")
    private Date createdBy;

    @Column(name = "UpdatedBy")
    private Date updatedBy;

    @Column(name = "CreateAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
