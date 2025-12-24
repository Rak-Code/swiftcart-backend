package com.ecommerce.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private String addressId;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
}
