package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ReviewRequestDTO;
import com.ecommerce.project.dto.ReviewResponseDTO;
import com.ecommerce.project.entity.Review;
import com.ecommerce.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> add(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.status(201).body(reviewService.addReview(dto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> productReviews(
            @PathVariable String productId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        // Handle pagination if page and size are provided
        if (page != null && size != null) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<ReviewResponseDTO> reviewPage = reviewService.getProductReviews(productId, pageable);
            return ResponseEntity.ok(reviewPage);
        }
        
        // Default: return all reviews without pagination
        List<ReviewResponseDTO> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> userReviews(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    @GetMapping("/can-review/{userId}/{productId}")
    public ResponseEntity<Boolean> canUserReviewProduct(@PathVariable String userId, @PathVariable String productId) {
        boolean canReview = reviewService.canUserReviewProduct(userId, productId);
        return ResponseEntity.ok(canReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable String reviewId, @RequestBody UpdateReviewRequest req) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, req.rating(), req.comment()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    record UpdateReviewRequest(int rating, String comment) {}
}
