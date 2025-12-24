package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private String id;

    @Indexed
    private String orderId;

    private double amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus = PaymentStatus.pending;

    private String transactionId;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private LocalDateTime paymentDate = LocalDateTime.now();

    public enum PaymentMethod {
        credit_card, debit_card, upi, net_banking, cod, razorpay
    }

    public enum PaymentStatus {
        pending, completed, failed, refunded
    }
}
