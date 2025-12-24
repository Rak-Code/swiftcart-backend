package com.ecommerce.project.controller;

import com.ecommerce.project.dto.RazorpayOrderRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderResponseDTO;
import com.ecommerce.project.dto.RazorpayPaymentVerificationDTO;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/razorpay/create")
    public ResponseEntity<RazorpayOrderResponseDTO> createRazorpayOrder(@RequestBody RazorpayOrderRequestDTO dto) {
        RazorpayOrderResponseDTO response = razorpayService.createRazorpayOrder(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<Payment> verifyPayment(@RequestBody RazorpayPaymentVerificationDTO dto) {
        Payment payment = razorpayService.verifyPayment(dto);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/razorpay/refund")
    public ResponseEntity<Map<String, String>> refundPayment(@RequestBody Map<String, Object> request) {
        String paymentId = (String) request.get("paymentId");
        double amount = ((Number) request.get("amount")).doubleValue();
        
        String refundId = razorpayService.refundPayment(paymentId, amount);
        return ResponseEntity.ok(Map.of("refundId", refundId, "message", "Refund processed successfully"));
    }
}
