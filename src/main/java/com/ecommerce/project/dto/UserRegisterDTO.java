package com.ecommerce.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserRegisterDTO(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        @Schema(description = "Full name of the user", example = "John Doe")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Schema(description = "Password for the user account (min 8 chars, must include uppercase, lowercase, digit, special char)", example = "SecurePass123!")
        String password,

        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
        @Schema(description = "Phone number of the user", example = "+1234567890")
        String phone,

        @Pattern(regexp = "^(USER|ADMIN|user|admin)?$", message = "Role must be either USER or ADMIN")
        @Schema(description = "Role of the user (USER or ADMIN)", example = "USER")
        String role
) {}
