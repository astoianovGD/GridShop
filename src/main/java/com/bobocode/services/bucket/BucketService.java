package com.bobocode.services.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.products.Product;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.bucket.BucketItemMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.products.ProductRepository;
import com.bobocode.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing user buckets.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BucketService {

    /**
     * Repository for managing bucket entities.
     */
    private final BucketRepository bucketRepository;

    /**
     * Repository for managing bucket item entities.
     */
    private final BucketItemRepository bucketItemRepository;

    /**
     * Repository for managing product entities.
     */
    private final ProductRepository productRepository;

    /**
     * Repository for managing user entities.
     */
    private final UserRepository userRepository;

    /**
     * Mapper for converting bucket items to DTOs.
     */
    private final BucketItemMapper bucketItemMapper;

    /**
     * Adds a product to the specified user's bucket.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to be added
     * @param amount    the amount of the product to add
     */
    @Transactional
    public void addProductToBucket(
            final long userId, final long productId, final int amount
    ) {
        Bucket bucket = getOrCreateBucket(userId);

        Product product = productRepository
                .findProductByIsActiveAndId(true, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with ID " + productId + " not found!"
                ));

        BucketItem bucketItem = bucketItemRepository
                .findByBucketIdAndProductId(bucket.getId(), productId)
                .orElse(null);

        if (bucketItem != null) {
            bucketItem.setQuantity(bucketItem.getQuantity() + amount);
        } else {
            bucketItem = new BucketItem();
            bucketItem.setBucket(bucket);
            bucketItem.setProduct(product);
            bucketItem.setQuantity(amount);
            bucket.getItems().add(bucketItem);
        }

        bucketItemRepository.save(bucketItem);
    }

    /**
     * Removes a product from the specified user's bucket.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to be removed
     */
    @Transactional
    public void removeProductFromBucket(
            final long userId, final long productId
    ) {
        Bucket bucket = bucketRepository.findByUserId(userId).orElse(null);
        if (bucket == null) {
            return;
        }

        bucketItemRepository.deleteByBucketIdAndProductId(
                bucket.getId(), productId
        );
    }

    /**
     * Retrieves the list of items from the specified user's bucket as DTOs.
     *
     * @param userId the ID of the user
     * @return a list of bucket item DTOs containing product info and quantity
     */
    @Transactional(readOnly = true)
    public List<BucketItemDto> getProductsFromBucket(final long userId) {
        Bucket bucket = bucketRepository.findByUserId(userId).orElse(null);
        if (bucket == null) {
            return List.of();
        }

        return bucket.getItems().stream()
                .map(bucketItemMapper::toDto)
                .toList();
    }

    /**
     * Helper method to get bucket, or create a new one if it doesn't exist.
     *
     * @param userId the ID of the user
     * @return the Bucket entity
     */
    @Transactional
    public Bucket getOrCreateBucket(final long userId) {
        return bucketRepository.findByUserId(userId)
                .orElseGet(() -> {
                    var user = userRepository
                            .findUserByIdAndRoleNameAndIsActive(
                                    userId, "USER", true
                            )
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "User with ID " + userId + " not found!"
                            ));

                    Bucket newBucket = new Bucket();
                    newBucket.setUser(user);

                    return bucketRepository.saveAndFlush(newBucket);
                });
    }

    /**
     * Clears all items from the specified user's bucket.
     *
     * @param userId the ID of the user
     */
    @Transactional
    public void clearBucket(final long userId) {
        bucketRepository.findByUserId(userId)
                .ifPresent(bucket -> bucketItemRepository
                        .deleteAllByBucketId(bucket.getId()));
    }
}
