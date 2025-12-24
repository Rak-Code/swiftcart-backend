package com.ecommerce.project.dto;

public record RazorpayOrderRequestDTO(
        String orderId,
        double amount
) {}
