package com.ecommerce.project.service;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.dto.ReviewResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Review;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ReviewRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public Review addReview(ReviewRequestDTO dto) {

        if (dto.rating() < 1 || dto.rating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setProductId(dto.productId());
        review.setUserId(dto.userId());
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        Review saved = reviewRepository.save(review);
        log.info("Added review for product {} by user {}", dto.productId(), dto.userId());
        
        return saved;
    }

    @Override
    public Review getReview(String reviewId) {
        log.info("Fetching review with ID: {}", reviewId);
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public List<ReviewResponseDTO> getProductReviews(String productId) {
        log.info("Fetching reviews for product: {}", productId);
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return convertToResponseDTOs(reviews);
    }

    @Override
    public Page<ReviewResponseDTO> getProductReviews(String productId, Pageable pageable) {
        log.info("Fetching reviews for product: {} with pagination", productId);
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        List<ReviewResponseDTO> responseDTOs = convertToResponseDTOs(reviewPage.getContent());
        return new PageImpl<>(responseDTOs, pageable, reviewPage.getTotalElements());
    }

    private List<ReviewResponseDTO> convertToResponseDTOs(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return List.of();
        }

        // Get all unique user IDs
        List<String> userIds = reviews.stream()
                .map(Review::getUserId)
                .distinct()
                .toList();

        // Fetch all users in one query
        Map<String, String> userIdToNameMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));

        // Convert reviews to DTOs
        return reviews.stream()
                .map(review -> new ReviewResponseDTO(
                        review.getId(),
                        review.getProductId(),
                        review.getUserId(),
                        userIdToNameMap.getOrDefault(review.getUserId(), "Unknown User"),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public List<Review> getUserReviews(String userId) {
        log.info("Fetching reviews by user: {}", userId);
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Review updateReview(String reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(rating);
        review.setComment(comment);

        Review updated = reviewRepository.save(review);
        
        log.info("Updated review {}", reviewId);
        
        return updated;
    }

    @Override
    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        String productId = review.getProductId();
        String userId = review.getUserId();
        
        reviewRepository.deleteById(reviewId);
        
        log.info("Deleted review {}", reviewId);
    }

    @Override
    public boolean canUserReviewProduct(String userId, String productId) {
        // Check if user has a delivered order containing this product
        List<Order> userOrders = orderRepository.findByUserIdAndStatus(userId, Order.Status.delivered);
        
        boolean hasDeliveredProduct = userOrders.stream()
                .anyMatch(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProductId() != null && item.getProductId().equals(productId)));
        
        if (!hasDeliveredProduct) {
            log.debug("User {} cannot review product {} - no delivered orders found", userId, productId);
            return false;
        }
        
        // Check if user has already reviewed this product
        boolean hasAlreadyReviewed = hasUserReviewedProduct(userId, productId);
        
        log.debug("User {} can review product {}: {}", userId, productId, !hasAlreadyReviewed);
        return !hasAlreadyReviewed;
    }

    @Override
    public boolean hasUserReviewedProduct(String userId, String productId) {
        boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(userId, productId);
        log.debug("User {} has reviewed product {}: {}", userId, productId, hasReviewed);
        return hasReviewed;
    }
}
