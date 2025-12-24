package com.ecommerce.project.controller;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartItem> add(@RequestBody AddCartRequest req) {
        return ResponseEntity.status(201).body(cartService.addToCart(req.userId(), req.productId(), req.quantity()));
    }

    @PutMapping
    public ResponseEntity<CartItem> updateQty(@RequestBody UpdateCartRequest req) {
        return ResponseEntity.ok(cartService.updateQuantity(req.userId(), req.productId(), req.quantity()));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> remove(@PathVariable String cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getUserCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clear(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    record AddCartRequest(String userId, String productId, int quantity) {}
    record UpdateCartRequest(String userId, String productId, int quantity) {}
}
