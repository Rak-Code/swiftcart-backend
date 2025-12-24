package com.ecommerce.project.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        String id,
        String productId,
        String userId,
        String userName,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
}