package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AuthResponseDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.security.JwtUtil;
import com.ecommerce.project.service.UserService;
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
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
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
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
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
