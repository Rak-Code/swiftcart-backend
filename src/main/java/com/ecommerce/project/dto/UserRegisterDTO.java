package com.ecommerce.project.dto;

import jakarta.validation.constraints.*;

public record UserRegisterDTO(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password,

        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
        String phone,

        @Pattern(regexp = "^(USER|ADMIN|user|admin)?$", message = "Role must be either USER or ADMIN")
        String role
) {}
