package com.ecommerce.project.config;

import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data Initializer to create default admin user on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@swiftcart.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.fullName:System Administrator}")
    private String adminFullName;

    @Value("${admin.phone:+1234567890}")
    private String adminPhone;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminUser();
    }

    private void createDefaultAdminUser() {
        // Check if admin user already exists
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists with email: {}", adminEmail);
            return;
        }

        // Create default admin user
        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
        adminUser.setFullName(adminFullName);
        adminUser.setPhone(adminPhone);
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setCreatedAt(LocalDateTime.now());

        try {
            User savedAdmin = userRepository.save(adminUser);
            log.info("Default admin user created successfully with ID: {} and email: {}", 
                    savedAdmin.getId(), savedAdmin.getEmail());
            
            // Only show warning if using default password
            if ("admin123".equals(adminPassword)) {
                log.warn("SECURITY WARNING: Using default admin password 'admin123'. Please change it immediately!");
            }
        } catch (Exception e) {
            log.error("Failed to create default admin user: {}", e.getMessage(), e);
        }
    }
}