package com.bobocode.entities.bucket;

import com.bobocode.entities.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping bucket.
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
     * The user who owns the bucket.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * The list of items contained in the bucket.
     */
    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BucketItem> items = new ArrayList<>();
}
