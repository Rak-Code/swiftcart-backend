package com.ecommerce.project.controller;

import com.ecommerce.project.dto.UserUpdateDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        // Handle pagination if page and size are provided
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<User> userPage = userService.getAllUsers(pageable);
            return ResponseEntity.ok(userPage);
        }
        
        // Default: return all users without pagination
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<User> getUserWithAddresses(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserWithAddresses(id));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<User> addAddress(@PathVariable String id, @RequestBody User.Address address) {
        User user = userService.addAddress(id, address);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id, @PathVariable String addressId) {
        userService.deleteAddress(id, addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserUpdateDTO dto) {
        User user = userService.updateUser(id, dto);
        return ResponseEntity.ok(user);
    }
}
