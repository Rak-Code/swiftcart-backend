package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddressRequestDTO;
import com.ecommerce.project.dto.AddressResponseDTO;

import java.util.List;

public interface AddressService {

    AddressResponseDTO addAddress(String userId, AddressRequestDTO addressRequestDTO);

    List<AddressResponseDTO> getAllAddresses(String userId);

    AddressResponseDTO getAddressById(String userId, String addressId);

    AddressResponseDTO updateAddress(String userId, String addressId, AddressRequestDTO addressRequestDTO);

    void deleteAddress(String userId, String addressId);

    AddressResponseDTO setDefaultAddress(String userId, String addressId);
}
