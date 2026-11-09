package com.bobocode.nosql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing product reviews and analytical aggregations in MongoDB.
 */
@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private static final int FIVE_STAR_RATING = 5;

    private final ProductReviewRepository reviewRepository;

    /**
     * Saves a new product review document in MongoDB.
     *
     * @param productId the product ID being reviewed
     * @param review    the review payload
     * @return the persisted {@link ProductReviewDocument}
     */
    public ProductReviewDocument addReview(
            final Long productId, final ProductReviewDocument review
    ) {
        review.setProductId(productId);
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        return reviewRepository.save(review);
    }

    /**
     * Retrieves reviews for a product, optionally filtered by minimum rating.
     *
     * @param productId the product ID
     * @param minRating optional minimum rating filter
     * @return list of review documents
     */
    public List<ProductReviewDocument> getReviewsByProduct(
            final Long productId, final Integer minRating
    ) {
        if (minRating != null) {
            return reviewRepository.findByProductIdAndRatingGreaterThanEqual(productId, minRating);
        }
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Computes analytical summary (total reviews, average rating, 5-star count)
     * for a given product from its MongoDB review documents.
     *
     * @param productId the product ID
     * @return aggregated {@link ProductAnalyticsSummaryDto}
     */
    public ProductAnalyticsSummaryDto getProductAnalytics(final Long productId) {
        List<ProductReviewDocument> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return new ProductAnalyticsSummaryDto(productId, 0L, 0.0, 0L);
        }

        long total = reviews.size();
        double avg = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(ProductReviewDocument::getRating)
                .average()
                .orElse(0.0);
        long fiveStars = reviews.stream()
                .filter(r -> r.getRating() != null && r.getRating() == FIVE_STAR_RATING)
                .count();

        return new ProductAnalyticsSummaryDto(productId, total, avg, fiveStars);
    }
}
