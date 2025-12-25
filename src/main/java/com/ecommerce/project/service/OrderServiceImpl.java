package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.exception.UnauthorizedException;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ReviewRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InvoiceService invoiceService;
    private final ReviewRepository reviewRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        log.info("Creating order for user: {}", dto.userId());

        Order order = new Order();
        order.setUserId(dto.userId());
        order.setAddress(dto.address());
        order.setTotalAmount(dto.totalAmount());
        order.setItems(dto.items());
        order.setStatus(Order.Status.pending);

        Order saved = orderRepository.save(order);
        log.info("Order saved with ID: {}", saved.getId());

        // Send emails asynchronously
        try {
            log.info("Looking up user for email sending, userId: {}", dto.userId());
            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.userId()));
            log.info("User found for email: {} ({})", user.getFullName(), user.getEmail());
            
            // Send confirmation email to customer
            log.info("Triggering customer confirmation email for order: {} to: {}", saved.getId(), user.getEmail());
            emailService.sendOrderConfirmationToCustomer(saved, user);
            log.info("Customer confirmation email method called for order: {}", saved.getId());
            
            // Send notification email to admin
            log.info("Triggering admin notification email for order: {}", saved.getId());
            emailService.sendOrderNotificationToAdmin(saved, user);
            log.info("Admin notification email method called for order: {}", saved.getId());
            
            log.info("All order email methods triggered successfully for order: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to trigger order emails for order: {}. Error: {}", saved.getId(), e.getMessage(), e);
            // Don't fail the order creation if email fails
        }

        return toDTO(saved);
    }

    @Override
    public OrderResponseDTO getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    public List<OrderResponseDTO> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toDTO)
                .toList();
    }

    @Override
    public Page<OrderResponseDTO> getUserOrders(String userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(String orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        Order.Status newStatus = Order.Status.valueOf(status.toLowerCase());
        Order.Status oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        // Generate and send invoice when order is delivered
        if (newStatus == Order.Status.delivered && oldStatus != Order.Status.delivered) {
            try {
                User user = userRepository.findById(order.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", order.getUserId()));
                
                // Generate invoice (this will be done asynchronously in a separate thread)
                generateAndSendInvoice(savedOrder, user);
                
                log.info("Invoice generation triggered for delivered order: {}", orderId);
            } catch (Exception e) {
                log.error("Failed to trigger invoice generation for order: {}", orderId, e);
                // Don't fail the status update if invoice generation fails
            }
        }

        return toDTO(savedOrder);
    }

    @Async
    private void generateAndSendInvoice(Order order, User user) {
        try {
            log.info("Async invoice generation started for order: {}", order.getId());
            
            // Generate invoice
            com.ecommerce.project.entity.Invoice invoice = invoiceService.generateInvoice(order, user);
            
            // Download PDF data
            byte[] pdfData = invoiceService.downloadInvoicePdf(invoice.getId());
            
            // Send emails with invoice attachment
            emailService.sendInvoiceToCustomer(invoice, user, pdfData);
            emailService.sendInvoiceToAdmin(invoice, user, pdfData);
            
            log.info("Invoice generated and emailed successfully for order: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to generate and send invoice for order: {}", order.getId(), e);
        }
    }

    @Override
    public void cancelOrder(String orderId, String userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedException("Order does not belong to user");
        }

        if (order.getStatus() != Order.Status.pending && order.getStatus() != Order.Status.processing) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        order.setStatus(Order.Status.cancelled);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private OrderResponseDTO toDTO(Order o) {
        return new OrderResponseDTO(
                o.getId(),
                o.getUserId(),
                o.getAddress(),
                o.getTotalAmount(),
                o.getStatus(),
                o.getOrderDate(),
                o.getItems()
        );
    }

    @Override
    public List<String> getReviewableProductsForUser(String userId) {
        // Get all delivered orders for the user
        List<Order> deliveredOrders = orderRepository.findByUserIdAndStatus(userId, Order.Status.delivered);
        
        // Extract all product IDs from delivered orders, filtering out nulls
        List<String> deliveredProductIds = deliveredOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(Order.OrderItem::getProductId)
                .filter(productId -> productId != null) // Filter out null productIds
                .distinct()
                .toList();
        
        // Filter out products that the user has already reviewed
        List<String> reviewableProductIds = deliveredProductIds.stream()
                .filter(productId -> !reviewRepository.existsByUserIdAndProductId(userId, productId))
                .toList();
        
        log.info("Found {} reviewable products out of {} delivered products for user {}", 
                reviewableProductIds.size(), deliveredProductIds.size(), userId);
        return reviewableProductIds;
    }
}
