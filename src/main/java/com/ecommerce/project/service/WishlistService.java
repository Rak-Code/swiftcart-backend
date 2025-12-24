package com.ecommerce.project.service;

import com.ecommerce.project.entity.WishlistItem;
import java.util.List;

public interface WishlistService {

    WishlistItem addToWishlist(String userId, String productId);

    void removeFromWishlist(String wishlistItemId);

    List<WishlistItem> getUserWishlist(String userId);

    void clearWishlist(String userId);

    boolean isInWishlist(String userId, String productId);
}
