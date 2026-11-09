package com.bobocode.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MongoDB document representing a customer product review with flexible schema-less
 * category-specific attributes (e.g., fabric/fit for clothing, battery/CPU for electronics).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_reviews")
public class ProductReviewDocument {

    /**
     * Unique MongoDB document identifier.
     */
    @Id
    private String id;

    /**
     * ID of the reviewed product (indexed for fast lookup).
     */
    @Indexed
    private Long productId;

    /**
     * ID of the user who submitted the review.
     */
    private Long userId;

    /**
     * Star rating from 1 to 5.
     */
    private Integer rating;

    /**
     * Free-text review comment.
     */
    private String comment;

    /**
     * Flexible schema-less key-value attributes specific to the product type
     * (e.g. {"size_fit": "true to size", "material_quality": "excellent"} or
     * {"battery_hours": 14, "noise_level": "silent"}).
     */
    private Map<String, Object> dynamicAttributes;

    /**
     * Timestamp when the review was created.
     */
    private LocalDateTime createdAt;
}
