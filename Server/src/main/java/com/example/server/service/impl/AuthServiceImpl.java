package com.example.server.service.impl;

import com.example.server.model.Users;
import com.example.server.repository.UsersRepository;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.response.JwtResponse;
import com.example.server.response.MessageResponse;
import com.example.server.security.jwt.JwtUtil;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public MessageResponse register(RegisterRequest registerRequest) throws ParseException {
        if (usersRepository.existsUsersByEmail(registerRequest.getEmail())) {
            return new MessageResponse("Email already exists", 1);
        }
        Users users = new Users();
        if (registerRequest.getRole() == null) {
            return new MessageResponse("Role is not found", 1);
        }
        users.setEmail(registerRequest.getEmail());
        users.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        users.setIsActive(true);
        users.setName(registerRequest.getName());
        users.setRole(registerRequest.getRole());
        users.setCreatedBy(registerRequest.getCreatedBy());
        users.setCreatedBy(registerRequest.getUpdatedBy());
        users.setCreatedAt(new Date());
        users.setUpdatedAt(new Date());
        usersRepository.save(users);
        return new MessageResponse("Account register successfully", 0);
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) throws InterruptedException {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return null;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.createToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return new JwtResponse(jwt, userDetails.getName(), userDetails.getEmail(), roles, 0);
    }

    @Override
    public MessageResponse logout(String token) {
        Users users = getPrincipal(token);
        return new MessageResponse("ok", 0);
    }

    @Override
    public Users getPrincipal(String token) {
        String parseToken = token.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromJwtToken(parseToken);
        return usersRepository.findByEmail(email).orElse(null);
    }
}
