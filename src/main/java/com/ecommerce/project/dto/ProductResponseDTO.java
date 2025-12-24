package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Product;
import java.util.List;

public record ProductResponseDTO(
        String id,
        String name,
        String description,
        double price,
        int stockQuantity,
        String categoryId,
        String color,
        Product.Size size,
        List<String> imageUrls
) {}
