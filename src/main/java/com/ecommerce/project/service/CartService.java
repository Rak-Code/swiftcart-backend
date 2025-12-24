package com.ecommerce.project.service;

import com.ecommerce.project.entity.CartItem;
import java.util.List;

public interface CartService {

    CartItem addToCart(String userId, String productId, int quantity);

    CartItem updateCartQuantity(String cartItemId, int quantity);

    CartItem updateQuantity(String userId, String productId, int quantity);

    void removeFromCart(String cartItemId);

    List<CartItem> getUserCart(String userId);

    void clearCart(String userId);
}
