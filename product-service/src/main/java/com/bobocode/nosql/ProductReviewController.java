package com.bobocode.nosql;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing schema-less product reviews and rating analytics in MongoDB.
 */
@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    /**
     * Adds a new review (with optional dynamic attributes) for a product.
     *
     * @param productId the product ID
     * @param review    the review document payload
     * @return the saved review document with HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<ProductReviewDocument> addReview(
            @PathVariable final Long productId,
            @RequestBody final ProductReviewDocument review
    ) {
        ProductReviewDocument saved = reviewService.addReview(productId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Retrieves all reviews for a product, optionally filtered by {@code minRating}.
     *
     * @param productId the product ID
     * @param minRating optional minimum rating filter
     * @return list of review documents
     */
    @GetMapping
    public ResponseEntity<List<ProductReviewDocument>> getReviews(
            @PathVariable final Long productId,
            @RequestParam(required = false) final Integer minRating
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, minRating));
    }

    /**
     * Retrieves aggregated rating analytics for a product from MongoDB.
     *
     * @param productId the product ID
     * @return analytical summary DTO
     */
    @GetMapping("/analytics")
    public ResponseEntity<ProductAnalyticsSummaryDto> getAnalytics(
            @PathVariable final Long productId
    ) {
        return ResponseEntity.ok(reviewService.getProductAnalytics(productId));
    }
}
