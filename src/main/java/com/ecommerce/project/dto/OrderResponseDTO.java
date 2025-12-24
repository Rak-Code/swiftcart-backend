package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        String id,
        String userId,
        Order.Address address,
        double totalAmount,
        Order.Status status,
        LocalDateTime orderDate,
        List<Order.OrderItem> items
) {}
