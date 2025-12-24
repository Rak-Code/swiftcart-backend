package com.ecommerce.project.repository;

import com.ecommerce.project.entity.WishlistItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends MongoRepository<WishlistItem, String> {

    List<WishlistItem> findByUserId(String userId);

    boolean existsByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    void deleteByUserId(String userId);
}
