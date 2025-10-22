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

    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    private Role role;

    @Column(name = "IsActive")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private Users createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private Users updatedBy;

    @Column(name = "CreateAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
