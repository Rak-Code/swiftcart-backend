package com.ecommerce.project.controller;

import com.ecommerce.project.entity.WishlistItem;
import com.ecommerce.project.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistItem> add(@RequestBody WishlistRequest req) {
        return ResponseEntity.status(201).body(wishlistService.addToWishlist(req.userId(), req.productId()));
    }

    @DeleteMapping("/{wishlistItemId}")
    public ResponseEntity<Void> remove(@PathVariable String wishlistItemId) {
        wishlistService.removeFromWishlist(wishlistItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistItem>> getUserWishlist(@PathVariable String userId) {
        return ResponseEntity.ok(wishlistService.getUserWishlist(userId));
    }

    record WishlistRequest(String userId, String productId) {}
}
