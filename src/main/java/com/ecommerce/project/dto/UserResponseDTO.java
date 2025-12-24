package com.ecommerce.project.dto;

import com.ecommerce.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private User.Role role;
    private LocalDateTime createdAt;
    private List<User.Address> addresses;
}
