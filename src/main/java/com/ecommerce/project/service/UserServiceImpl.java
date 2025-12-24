package com.ecommerce.project.service;

import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserUpdateDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.DuplicateResourceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.exception.UnauthorizedException;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(UserRegisterDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Email already exists: " + dto.email());
        }

        // Validate password strength
        PasswordValidator.validate(dto.password());

        User user = new User();
        user.setFullName(dto.fullName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));

        // Set role: if role is provided and equals "admin" (case-insensitive), set ADMIN, otherwise default to USER
        if (dto.role() != null && !"".equals(dto.role().trim()) &&
            "admin".equalsIgnoreCase(dto.role().trim())) {
            user.setRole(User.Role.ADMIN);
        }
        // If role is not provided or invalid, keep default USER role

        User saved = userRepository.save(user);
        log.info("Registered new user with ID: {}", saved.getId());
        
        return saved;
    }

    @Override
    public User login(UserLoginDTO dto) {

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        log.info("User {} logged in successfully", user.getId());
        return user;
    }

    @Override
    public User getUserById(String userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public java.util.List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserWithAddresses(String userId) {
        log.info("Fetching user with addresses for ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public User addAddress(String userId, User.Address address) {

        User user = getUserById(userId);

        user.getAddresses().add(address);

        User updated = userRepository.save(user);
        log.info("Added address for user {}", userId);
        
        return updated;
    }

    @Override
    public void deleteAddress(String userId, String addressId) {
        User user = getUserById(userId);
        user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));
        userRepository.save(user);
        log.info("Deleted address {} for user {}", addressId, userId);
    }

    @Override
    public User updateUser(String userId, UserUpdateDTO dto) {
        User user = getUserById(userId);

        // Update phone if provided
        if (dto.phone() != null && !dto.phone().trim().isEmpty()) {
            user.setPhone(dto.phone());
        }

        User updated = userRepository.save(user);
        log.info("Updated user with ID: {}", userId);
        
        return updated;
    }
}
