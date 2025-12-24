package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProductId(String productId);

    Page<Review> findByProductId(String productId, Pageable pageable);

    List<Review> findByUserId(String userId);

    boolean existsByUserIdAndProductId(String userId, String productId);
}
