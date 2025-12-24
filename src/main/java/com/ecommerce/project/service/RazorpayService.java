package com.ecommerce.project.service;

import com.ecommerce.project.dto.RazorpayOrderRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderResponseDTO;
import com.ecommerce.project.dto.RazorpayPaymentVerificationDTO;
import com.ecommerce.project.entity.Payment;

public interface RazorpayService {

    RazorpayOrderResponseDTO createRazorpayOrder(RazorpayOrderRequestDTO dto);

    Payment verifyPayment(RazorpayPaymentVerificationDTO dto);

    String refundPayment(String paymentId, double amount);
}
