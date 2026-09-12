package com.bobocode.repositories.bucket;

import com.bobocode.entities.bucket.BucketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link BucketItem} entities.
 */
@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {

    /**
     * Finds a bucket item by bucket ID and product ID.
     *
     * @param bucketId  the bucket ID
     * @param productId the product ID
     * @return an optional containing the bucket item if found
     */
    Optional<BucketItem> findByBucketIdAndProductId(
            Long bucketId, Long productId
    );

    /**
     * Deletes a bucket item by bucket ID and product ID.
     *
     * @param bucketId  the bucket ID
     * @param productId the product ID
     */
    void deleteByBucketIdAndProductId(Long bucketId, Long productId);

    /**
     * Deletes all bucket items belonging to a specified bucket ID.
     *
     * @param bucketId the bucket ID
     */
    void deleteAllByBucketId(Long bucketId);

    /**
     * Deletes all bucket items associated with a given product ID.
     *
     * @param productId the product ID
     */
    @Modifying
    @Query("DELETE FROM BucketItem b WHERE b.productId = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);
}
