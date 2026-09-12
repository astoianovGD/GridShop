package com.bobocode.entities.bucket;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping bucket in order-service.
 */
@Data
@Entity
@Table(name = "bucket")
public class Bucket {

    /**
     * The unique identifier of the bucket.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_id")
    private Long id;

    /**
     * The user ID who owns the bucket.
     */
    @Column(name = "user_id", nullable = false, unique = true)
    @NotNull
    private Long userId;

    /**
     * The list of items contained in the bucket.
     */
    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BucketItem> items = new ArrayList<>();
}
