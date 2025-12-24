package com.ecommerce.project.dto;

public record ReviewRequestDTO(
        String productId,
        String userId,
        int rating,
        String comment
) {}
