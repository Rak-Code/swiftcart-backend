package com.ecommerce.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginDTO(
        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,
        @Schema(description = "Password for the user account", example = "password123")
        String password
) {}
