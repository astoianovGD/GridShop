package com.bobocode.entities.bucket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents an individual item inside a user's shopping bucket.
 */
@Data
@Entity
@Table(name = "bucket_items")
public class BucketItem {

    /**
     * The unique identifier of the bucket item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_item_id")
    private Long bucketItemId;

    /**
     * The bucket to which this item belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_id", nullable = false)
    @NotNull
    private Bucket bucket;

    /**
     * The product ID associated with this bucket item.
     */
    @Column(name = "product_id", nullable = false)
    @NotNull
    private Long productId;

    /**
     * The quantity of the product in the bucket.
     */
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity;
}
