package com.ecommerce.project.dto;

import com.ecommerce.project.entity.Order;
import java.util.List;

public record OrderRequestDTO(
        String userId,
        Order.Address address,
        double totalAmount,
        List<Order.OrderItem> items
) {}
