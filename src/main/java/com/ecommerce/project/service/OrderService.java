package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO dto);

    OrderResponseDTO getOrder(String orderId);

    List<OrderResponseDTO> getUserOrders(String userId);

    Page<OrderResponseDTO> getUserOrders(String userId, Pageable pageable);

    OrderResponseDTO updateOrderStatus(String orderId, String status);

    void cancelOrder(String orderId, String userId);

    List<OrderResponseDTO> getAllOrders();

    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    List<String> getReviewableProductsForUser(String userId);
}
