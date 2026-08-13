package com.bobocode.entities.bucket;

import com.bobocode.entities.products.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents an individual
 * item inside a user's shopping bucket.
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
    private long bucketItemId;

    /**
     * The bucket to which this item belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_id", nullable = false)
    private Bucket bucket;

    /**
     * The product associated with this bucket item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The quantity of the product in the bucket.
     */
    @Column(nullable = false)
    private int quantity;
}
