package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AuthResponseDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.security.JwtUtil;
import com.ecommerce.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided registration details"
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data",
        content = @Content)
    @ApiResponse(responseCode = "409", description = "User already exists",
        content = @Content)
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody @Parameter(schema = @Schema(implementation = UserRegisterDTO.class)) UserRegisterDTO dto) {
        User saved = userService.register(dto);
        UserResponseDTO response = new UserResponseDTO(
            saved.getId(),
            saved.getEmail(),
            saved.getFullName(),
            saved.getPhone(),
            saved.getRole(),
            saved.getCreatedAt(),
            saved.getAddresses()
        );
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns JWT token"
    )
    @ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
        content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid input data",
        content = @Content)
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody @Parameter(schema = @Schema(implementation = UserLoginDTO.class)) UserLoginDTO dto) {
        User user = userService.login(dto);
        // Generate token
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        String token = jwtUtil.generateToken(userDetails);
        
        UserResponseDTO userResponse = new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getRole(),
            user.getCreatedAt(),
            user.getAddresses()
        );
        
        return ResponseEntity.ok(new AuthResponseDTO(token, userResponse));
    }
}
