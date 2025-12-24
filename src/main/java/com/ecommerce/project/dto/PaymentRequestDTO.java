package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Payment;

public record PaymentRequestDTO(
        String orderId,
        double amount,
        Payment.PaymentMethod paymentMethod
) {}
