package com.ecommerce.project.dto;

import java.time.LocalDateTime;

public record InvoiceResponseDTO(
        String id,
        String invoiceNumber,
        String orderId,
        String userId,
        String customerName,
        String customerEmail,
        double totalAmount,
        double taxAmount,
        double subtotal,
        LocalDateTime invoiceDate,
        LocalDateTime generatedAt,
        String pdfPath,
        boolean emailedToCustomer,
        boolean emailedToAdmin
) {
}
