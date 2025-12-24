package com.ecommerce.project.service;

import com.ecommerce.project.entity.WishlistItem;
import com.ecommerce.project.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ReminderSchedulerService reminderSchedulerService;

    @Value("${reminder.wishlist.delay-minutes:60}")
    private int wishlistReminderDelayMinutes;

    @Override
    public WishlistItem addToWishlist(String userId, String productId) {

        // Check if item already exists
        if (isInWishlist(userId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        WishlistItem item = new WishlistItem();
        item.setUserId(userId);
        item.setProductId(productId);

        WishlistItem savedItem = wishlistRepository.save(item);

        // Schedule reminder email
        reminderSchedulerService.scheduleWishlistReminder(userId, productId, wishlistReminderDelayMinutes);

        return savedItem;
    }

    @Override
    public void removeFromWishlist(String wishlistItemId) {
        wishlistRepository.deleteById(wishlistItemId);
    }

    @Override
    public List<WishlistItem> getUserWishlist(String userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Override
    public void clearWishlist(String userId) {
        wishlistRepository.deleteByUserId(userId);
    }

    @Override
    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
