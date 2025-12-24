package com.ecommerce.project.service;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(PaymentRequestDTO dto) {

        // Simulate payment processing
        Payment payment = new Payment();
        payment.setOrderId(dto.orderId());
        payment.setAmount(dto.amount());
        payment.setPaymentMethod(dto.paymentMethod());
        payment.setPaymentStatus(Payment.PaymentStatus.completed);
        payment.setTransactionId("TXN_" + System.currentTimeMillis());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));
    }

    @Override
    public Payment refundPayment(String paymentId) {

        Payment payment = getPayment(paymentId);

        if (payment.getPaymentStatus() != Payment.PaymentStatus.completed) {
            throw new RuntimeException("Payment must be completed before refund");
        }

        payment.setPaymentStatus(Payment.PaymentStatus.refunded);

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
}
