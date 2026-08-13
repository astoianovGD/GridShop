package com.bobocode.repositories.bucket;

import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            long bucketId, long productId
    );

    /**
     * Deletes a bucket item by bucket ID and product ID.
     *
     * @param bucketId  the bucket ID
     * @param productId the product ID
     */
    void deleteByBucketIdAndProductId(long bucketId, long productId);

    /**
     * Deletes all bucket items belonging to a specified bucket ID.
     *
     * @param bucketId the bucket ID
     */
    void deleteAllByBucketId(long bucketId);

    /**
     * Finds active users who have an active product in their bucket.
     *
     * @param productId the product ID
     * @return a list of active users
     */
    @Query("""
    SELECT DISTINCT u
    FROM BucketItem i
    JOIN i.bucket b
    JOIN b.user u
    JOIN i.product p
    WHERE p.id = :productId
      AND p.isActive = true
      AND u.isActive = true
    """)
    List<User> findActiveUsersByActiveProductIdInBucket(
            @Param("productId") long productId
    );

    /**
     * Deletes all bucket items associated with a given product ID.
     *
     * @param productId the product ID
     */
    @Modifying
    @Query("DELETE FROM BucketItem b WHERE b.product.id = :productId")
    void deleteAllByProductId(@Param("productId") long productId);
}
