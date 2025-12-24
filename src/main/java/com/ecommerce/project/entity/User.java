package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private String fullName;

    private String phone;

    private Role role = Role.USER;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<Address> addresses = new ArrayList<>();

    // ENUM
    public enum Role { USER, ADMIN }

    // Embedded Address Model
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {

        private String addressId = java.util.UUID.randomUUID().toString();

        private String addressLine;

        private String city;

        private String state;

        private String postalCode;

        private String country;

        private boolean isDefault = false;
    }
}
