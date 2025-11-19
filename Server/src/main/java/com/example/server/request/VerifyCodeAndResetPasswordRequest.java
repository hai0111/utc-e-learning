package com.example.server.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeAndResetPasswordRequest {

    @NotBlank(message = "Email cannot be blank")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format (must be example@domain.com)"
    )
    private String email;

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = ".*[A-Z].*",
            message = "Password must contain at least one uppercase letter."
    )
    @Pattern(
            regexp = ".*\\d.*",
            message = "Password must contain at least one number."
    )
    @Pattern(
            regexp = ".*[@$!%*?&].*",
            message = "Password must contain at least one special character (@$!%*?&)."
    )
    private String newPassword;

    @NotBlank(message = "Confirm cannot be blank")
    private String confirmPassword;
}
