package com.example.server.security.service;

import com.example.server.model.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private UUID id;

    private String code;

    private String name;

    private String email;

    private String password;

    private Boolean isActive;

    private Users createdBy;

    private Users updatedBy;

    private Date createdAt;

    private Date updatedAt;

    private Collection<? extends GrantedAuthority> roles;

    public static UserDetailsImpl build(Users users) {
        GrantedAuthority role = new SimpleGrantedAuthority(users.getRole().name());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(role);
        return new UserDetailsImpl(
                users.getId(),
                users.getCode(),
                users.getName(),
                users.getEmail(),
                users.getPassword(),
                users.getIsActive(),
                users.getCreatedBy(),
                users.getUpdatedBy(),
                users.getCreatedAt(),
                users.getUpdatedAt(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
