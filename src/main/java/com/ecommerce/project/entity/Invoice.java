package com.ecommerce.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    private String id;

    @Indexed(unique = true)
    private String invoiceNumber;

    @Indexed
    private String orderId;

    @Indexed
    private String userId;

    private String customerName;
    private String customerEmail;

    private double totalAmount;
    private double taxAmount;
    private double subtotal;

    private LocalDateTime invoiceDate;
    private LocalDateTime generatedAt;

    private String pdfPath; // Path to stored PDF in R2/S3

    private boolean emailedToCustomer = false;
    private boolean emailedToAdmin = false;
}
