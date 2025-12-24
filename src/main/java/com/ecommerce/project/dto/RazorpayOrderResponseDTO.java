package com.ecommerce.project.dto;

public record RazorpayOrderResponseDTO(
        String razorpayOrderId,
        String orderId,
        double amount,
        String currency,
        String keyId
) {}
