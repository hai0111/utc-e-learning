package com.example.server.service.impl;

import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.Users;
import com.example.server.repository.UsersRepository;
import com.example.server.request.ChangePasswordRequest;
import com.example.server.request.ForgotPasswordRequest;
import com.example.server.request.LoginRequest;
import com.example.server.request.RegisterRequest;
import com.example.server.request.VerifyCodeAndResetPasswordRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.JwtResponse;
import com.example.server.response.UsersDetailsResponse;
import com.example.server.security.jwt.JwtUtil;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.AuthService;
import com.example.server.utils.MailUtil;
import com.example.server.utils.TempCodeUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TempCodeUtil tempCodeUtil;

    @Autowired
    private MailUtil mailUtil;

    @Override
    public ApiResponse<Users> register(RegisterRequest registerRequest) throws ParseException {
        if (usersRepository.existsUsersByEmail(registerRequest.getEmail())) {
            throw new CustomServiceException("Email already exists", HttpStatus.CONFLICT);
        }
        Users users = new Users();
        if (registerRequest.getRole() == null) {
            throw new CustomServiceException("Role is not found", HttpStatus.CONFLICT);
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
        Users savedUser = usersRepository.save(users);
        return new ApiResponse<>(200, "User registered successfully", savedUser);
    }

    @Override
    public ApiResponse<JwtResponse> login(LoginRequest loginRequest) {
        Authentication authentication = null;
        Users users = usersRepository.findByEmailAndIsActive(loginRequest.getEmail(), false);
        if (users != null) {
            throw new CustomServiceException("Account does not exist or has stopped working", HttpStatus.FORBIDDEN);
        }
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            throw new CustomServiceException("Email or password isn't correct", HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.createToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Role primaryRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleString -> {
                    try {
                        return Role.valueOf(roleString);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unknown role string: " + roleString);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        JwtResponse data = new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getCode(),
                userDetails.getName(),
                userDetails.getEmail(),
                primaryRole
        );
        return new ApiResponse<>(200, "Login success", data);
    }

    @Override
    public ApiResponse<UsersDetailsResponse> getPrincipalFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomServiceException("Not logged in yet", HttpStatus.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        UsersDetailsResponse usersDetailsResponse = UsersDetailsResponse.toUserDetailsResponse(userDetails, role);
        return new ApiResponse<>(200, "Success", usersDetailsResponse);
    }

    @Override
    public ApiResponse<Users> changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomServiceException("Not logged in yet", HttpStatus.UNAUTHORIZED);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users users = usersRepository.findByIdAndIsActive(userDetails.getId(), true);
        if (users == null) {
            throw new CustomServiceException("User not found", HttpStatus.NOT_FOUND);
        }
        String oldPassword = users.getPassword();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), oldPassword)) {
            throw new CustomServiceException("Old password does not match", HttpStatus.CONFLICT);
        }
        String validationError = validateNewPassword(changePasswordRequest.getNewPassword());

        if (validationError != null) {
            throw new CustomServiceException(validationError, HttpStatus.BAD_REQUEST);
        }

        String newHashedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        users.setPassword(newHashedPassword);
        usersRepository.save(users);
        return new ApiResponse<>(200, "Password changed successfully", null);
    }

    @Override
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequest forgotPasswordRequest,
    HttpServletRequest httpServletRequest) {
        Users users = usersRepository.findByEmailAndIsActive(forgotPasswordRequest.getEmail(), true);
        if (users == null) {
            throw new CustomServiceException("User not found", HttpStatus.NOT_FOUND);
        }
        try {
            String code = tempCodeUtil.createTemporaryCode(httpServletRequest.getRemoteAddr());
            String body = mailUtil.resetPassMailTemplate(forgotPasswordRequest.getEmail(), code);
            String subject = "Confirmation code for your password reset request";
            mailUtil.sendEmail(forgotPasswordRequest.getEmail(), subject, body);
            return new ApiResponse<>(200, "The code has been sent to your email.", null);
        } catch (Exception e) {
            throw new CustomServiceException("Email could not be sent", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ApiResponse<Void> verifyCodeAndSetNewPassword(VerifyCodeAndResetPasswordRequest request, HttpServletRequest httpServletRequest) {
        Boolean verifyCode = tempCodeUtil.verifyTemporaryCode(httpServletRequest.getRemoteAddr(), request.getCode());
        if (verifyCode) {
            tempCodeUtil.removeCode(httpServletRequest.getRemoteAddr());
            Users users = usersRepository.findByEmailAndIsActive(request.getEmail(), true);
            if (users == null) {
                throw new CustomServiceException("User not found", HttpStatus.NOT_FOUND);
            }
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmPassword();
            if (!newPassword.equals(confirmPassword)) {
                throw new CustomServiceException("Password and confirm password do not match", HttpStatus.CONFLICT);
            }
            users.setPassword(passwordEncoder.encode(newPassword));
            usersRepository.save(users);
            return new ApiResponse<>(200, "Password verified successfully", null);
        }
        return null;
    }

    private String validateNewPassword(String password) {
        // Regex for each condition
        final String LENGTH_REGEX = ".{8,20}";
        final String LOWERCASE_REGEX = ".*[a-z].*";
        final String UPPERCASE_REGEX = ".*[A-Z].*";
        final String DIGIT_REGEX = ".*\\d.*";
        final String SPECIAL_CHAR_REGEX = ".*[^a-zA-Z0-9].*";

        // Check the length
        if (!password.matches(LENGTH_REGEX)) {
            return "Password must be between 8 and 20 characters long.";
        }

        // Check lowercase
        if (!password.matches(LOWERCASE_REGEX)) {
            return "Password must contain at least one lowercase letter (a-z).";
        }

        // Check uppercase
        if (!password.matches(UPPERCASE_REGEX)) {
            return "Password must contain at least one uppercase letter (A-Z).";
        }

        // Check number
        if (!password.matches(DIGIT_REGEX)) {
            return "Password must contain at least one digit (0-9).";
        }

        // Check for special characters
        if (!password.matches(SPECIAL_CHAR_REGEX)) {
            return "Password must contain at least one special character (e.g., !@#$%^&*).";
        }

        // If all conditions are satisfied
        return null;
    }
}
