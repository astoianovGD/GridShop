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

@Repository
public interface BucketItemRepository extends JpaRepository<BucketItem, Long> {

    Optional<BucketItem> findByBucketIdAndProductId(long bucketId, long productId);

    void deleteByBucketIdAndProductId(long bucketId, long productId);

    void deleteAllByBucketId(long bucketId);

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
    List<User> findActiveUsersByActiveProductIdInBucket(@Param("productId") long productId);

    @Modifying
    @Query("DELETE FROM BucketItem b WHERE b.product.id = :productId")
    void deleteAllByProductId(@Param("productId") long productId);

}
