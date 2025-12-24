package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AddressRequestDTO;
import com.ecommerce.project.dto.AddressResponseDTO;
import com.ecommerce.project.security.UserPrincipal;
import com.ecommerce.project.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing user addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Add a new address", description = "Add a new address for the authenticated user")
    public ResponseEntity<AddressResponseDTO> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        
        AddressResponseDTO response = addressService.addAddress(userPrincipal.getId(), addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Get all addresses for the authenticated user")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<AddressResponseDTO> addresses = addressService.getAllAddresses(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID", description = "Get a specific address by its ID")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        AddressResponseDTO address = addressService.getAddressById(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        
        AddressResponseDTO response = addressService.updateAddress(userPrincipal.getId(), addressId, addressRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address", description = "Delete an address")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        addressService.deleteAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/set-default")
    @Operation(summary = "Set default address", description = "Set an address as the default address")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        AddressResponseDTO response = addressService.setDefaultAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(response);
    }
}
