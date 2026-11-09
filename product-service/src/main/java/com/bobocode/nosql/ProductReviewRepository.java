package com.bobocode.nosql;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for {@link ProductReviewDocument}.
 */
@Repository
public interface ProductReviewRepository extends MongoRepository<ProductReviewDocument, String> {

    /**
     * Finds all reviews for a given product ID.
     *
     * @param productId the product ID
     * @return list of matching review documents
     */
    List<ProductReviewDocument> findByProductId(Long productId);

    /**
     * Finds reviews for a given product ID with a rating greater than or equal to {@code minRating}.
     *
     * @param productId the product ID
     * @param minRating minimum star rating
     * @return list of matching review documents
     */
    List<ProductReviewDocument> findByProductIdAndRatingGreaterThanEqual(
            Long productId, Integer minRating
    );
}
