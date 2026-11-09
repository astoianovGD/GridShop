package com.bobocode.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing aggregated analytical statistics for a product's reviews stored in MongoDB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAnalyticsSummaryDto {

    /**
     * ID of the product.
     */
    private Long productId;

    /**
     * Total number of reviews for the product.
     */
    private long totalReviews;

    /**
     * Average star rating across all reviews.
     */
    private double averageRating;

    /**
     * Number of 5-star reviews.
     */
    private long fiveStarCount;
}
