package com.ecommerce.project.service;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ReminderSchedulerService reminderSchedulerService;

    @Value("${reminder.cart.delay-minutes:30}")
    private int cartReminderDelayMinutes;

    @Override
    public CartItem addToCart(String userId, String productId, int quantity) {

        // Check if item already exists in cart
        CartItem existingItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartRepository.save(existingItem);
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);

        CartItem savedItem = cartRepository.save(cartItem);

        // Schedule reminder email
        reminderSchedulerService.scheduleCartReminder(userId, productId, cartReminderDelayMinutes);

        return savedItem;
    }

    @Override
    public CartItem updateCartQuantity(String cartItemId, int quantity) {

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Override
    public CartItem updateQuantity(String userId, String productId, int quantity) {
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for user: " + userId + " and product: " + productId));

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        item.setQuantity(quantity);

        return cartRepository.save(item);
    }

    @Override
    public void removeFromCart(String cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    @Override
    public List<CartItem> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
