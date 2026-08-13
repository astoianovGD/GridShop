package com.bobocode.repositories.bucket;

import com.bobocode.entities.bucket.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link Bucket} entities.
 */
@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {

    /**
     * Finds a shopping bucket associated with a specific user ID.
     *
     * @param userId the user ID
     * @return an optional containing the bucket if found
     */
    Optional<Bucket> findByUserId(long userId);
}
