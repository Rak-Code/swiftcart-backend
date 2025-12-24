package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressRequestDTO;
import com.ecommerce.project.dto.AddressResponseDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;

    @Override
    public AddressResponseDTO addAddress(String userId, AddressRequestDTO addressRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If this is set as default, unset all other defaults
        if (addressRequestDTO.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        // Create new address
        User.Address address = new User.Address();
        address.setAddressId(UUID.randomUUID().toString());
        address.setAddressLine(addressRequestDTO.getAddressLine());
        address.setCity(addressRequestDTO.getCity());
        address.setState(addressRequestDTO.getState());
        address.setPostalCode(addressRequestDTO.getPostalCode());
        address.setCountry(addressRequestDTO.getCountry());
        address.setDefault(addressRequestDTO.isDefault());

        user.getAddresses().add(address);
        userRepository.save(user);

        return mapToResponseDTO(address);
    }

    @Override
    public List<AddressResponseDTO> getAllAddresses(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getAddresses().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDTO getAddressById(String userId, String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User.Address address = user.getAddresses().stream()
                .filter(addr -> addr.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        return mapToResponseDTO(address);
    }

    @Override
    public AddressResponseDTO updateAddress(String userId, String addressId, AddressRequestDTO addressRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User.Address address = user.getAddresses().stream()
                .filter(addr -> addr.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // If this is set as default, unset all other defaults
        if (addressRequestDTO.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        address.setAddressLine(addressRequestDTO.getAddressLine());
        address.setCity(addressRequestDTO.getCity());
        address.setState(addressRequestDTO.getState());
        address.setPostalCode(addressRequestDTO.getPostalCode());
        address.setCountry(addressRequestDTO.getCountry());
        address.setDefault(addressRequestDTO.isDefault());

        userRepository.save(user);

        return mapToResponseDTO(address);
    }

    @Override
    public void deleteAddress(String userId, String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean removed = user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));

        if (!removed) {
            throw new RuntimeException("Address not found");
        }

        userRepository.save(user);
    }

    @Override
    public AddressResponseDTO setDefaultAddress(String userId, String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Unset all defaults
        user.getAddresses().forEach(addr -> addr.setDefault(false));

        // Set the specified address as default
        User.Address address = user.getAddresses().stream()
                .filter(addr -> addr.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setDefault(true);
        userRepository.save(user);

        return mapToResponseDTO(address);
    }

    private AddressResponseDTO mapToResponseDTO(User.Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}
