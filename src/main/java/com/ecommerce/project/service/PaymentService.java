package com.ecommerce.project.service;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    Payment processPayment(PaymentRequestDTO dto);

    Payment getPayment(String paymentId);

    Payment getPaymentByOrderId(String orderId);

    Payment refundPayment(String paymentId);

    List<Payment> getAllPayments();

    Page<Payment> getAllPayments(Pageable pageable);
}
