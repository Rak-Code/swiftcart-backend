package com.ecommerce.project.service;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.dto.ReviewResponseDTO;
import com.ecommerce.project.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Review addReview(ReviewRequestDTO dto);

    Review getReview(String reviewId);

    List<ReviewResponseDTO> getProductReviews(String productId);

    Page<ReviewResponseDTO> getProductReviews(String productId, Pageable pageable);

    List<Review> getUserReviews(String userId);

    Review updateReview(String reviewId, int rating, String comment);

    void deleteReview(String reviewId);

    boolean canUserReviewProduct(String userId, String productId);

    boolean hasUserReviewedProduct(String userId, String productId);
}
