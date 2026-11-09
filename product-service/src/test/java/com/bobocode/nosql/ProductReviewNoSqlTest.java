package com.bobocode.nosql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReviewNoSqlTest {

    @Mock
    private ProductReviewRepository reviewRepository;

    private ProductReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ProductReviewService(reviewRepository);
    }

    @Test
    @DisplayName("addReview should store schema-less dynamicAttributes in MongoDB")
    void shouldSaveReviewWithDynamicAttributes() {
        ProductReviewDocument review = new ProductReviewDocument(
                null,
                null,
                4L,
                5,
                "Awesome t-shirt!",
                Map.of("size_fit", "true to size", "material", "100% cotton", "shrinks_after_wash", false),
                null
        );
        when(reviewRepository.save(any(ProductReviewDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductReviewDocument saved = reviewService.addReview(1L, review);

        assertEquals(1L, saved.getProductId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("100% cotton", saved.getDynamicAttributes().get("material"));
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("getProductAnalytics should compute total reviews, average rating, and 5-star count")
    void shouldComputeAnalyticsFromMongoDocuments() {
        List<ProductReviewDocument> reviews = List.of(
                new ProductReviewDocument("r1", 2L, 1L, 5, "Great book", Map.of("pages", 420), LocalDateTime.now()),
                new ProductReviewDocument("r2", 2L, 2L, 4, "Good read", Map.of("edition", "2nd"), LocalDateTime.now()),
                new ProductReviewDocument("r3", 2L, 3L, 5, "Must read", Map.of("format", "hardcover"), LocalDateTime.now())
        );
        when(reviewRepository.findByProductId(2L)).thenReturn(reviews);

        ProductAnalyticsSummaryDto analytics = reviewService.getProductAnalytics(2L);

        assertEquals(2L, analytics.getProductId());
        assertEquals(3L, analytics.getTotalReviews());
        assertEquals(4.666, analytics.getAverageRating(), 0.01);
        assertEquals(2L, analytics.getFiveStarCount());
    }
}
