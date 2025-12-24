package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
        String name,

        @NotBlank(message = "Product description is required")
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description,

        @NotBlank(message = "Category ID is required")
        String categoryId,

        @Positive(message = "Price must be positive")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        double price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        List<String> imageUrls,

        Product.Size size,

        @Size(max = 50, message = "Color name must not exceed 50 characters")
        String color
) {}
