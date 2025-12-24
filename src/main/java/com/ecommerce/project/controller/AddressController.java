package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AddressRequestDTO;
import com.ecommerce.project.dto.AddressResponseDTO;
import com.ecommerce.project.security.UserPrincipal;
import com.ecommerce.project.service.AddressService;
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
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        
        AddressResponseDTO response = addressService.addAddress(userPrincipal.getId(), addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<AddressResponseDTO> addresses = addressService.getAllAddresses(userPrincipal.getId());
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        AddressResponseDTO address = addressService.getAddressById(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        
        AddressResponseDTO response = addressService.updateAddress(userPrincipal.getId(), addressId, addressRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        addressService.deleteAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/set-default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        
        AddressResponseDTO response = addressService.setDefaultAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(response);
    }
}
