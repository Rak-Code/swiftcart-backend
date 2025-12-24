package com.ecommerce.project.controller;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO created = orderService.createOrder(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> userOrders(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "orderDate") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        // Handle pagination if page and size are provided
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<OrderResponseDTO> orderPage = orderService.getUserOrders(userId, pageable);
            return ResponseEntity.ok(orderPage);
        }
        
        // Default: return all orders without pagination
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/user/{userId}/reviewable-products")
    public ResponseEntity<List<String>> getReviewableProducts(@PathVariable String userId) {
        List<String> reviewableProducts = orderService.getReviewableProductsForUser(userId);
        return ResponseEntity.ok(reviewableProducts);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> get(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        Order.Status status = Order.Status.valueOf(body.get("status").toLowerCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status.name()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> allOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "orderDate") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        // Handle pagination if page and size are provided
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<OrderResponseDTO> orderPage = orderService.getAllOrders(pageable);
            return ResponseEntity.ok(orderPage);
        }
        
        // Default: return all orders without pagination
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
