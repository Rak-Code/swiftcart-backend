package com.ecommerce.project.dto;

public record RazorpayPaymentVerificationDTO(
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature,
        String orderId
) {}
