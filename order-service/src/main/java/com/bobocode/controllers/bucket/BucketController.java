package com.bobocode.controllers.bucket;

import com.bobocode.dto.bucket.AddProductRequest;
import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.services.bucket.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/bucket")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public class BucketController {

    private final BucketService bucketService;

    /**
     * Get product from bucket.
     * GET /api/v1/users/1/bucket
     */
    @GetMapping
    public List<BucketItemDto> getBucketItemsByUserId(@PathVariable Long userId) {
        return bucketService.getProductsFromBucket(userId);
    }

    /**
     * Add product to bucket.
     * POST /api/v1/users/1/bucket
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToBucket(@PathVariable Long userId,
                                   @RequestBody @Validated AddProductRequest addProductRequest) {
        bucketService.addProductToBucket(userId, addProductRequest.getProductId(), addProductRequest.getAmount());
    }

    /**
     * Remove product from bucket.
     * DELETE /api/v1/users/1/bucket/items/5
     */
    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductFromBucket(@PathVariable Long userId,
                                        @PathVariable Long productId) {
        bucketService.removeProductFromBucket(userId, productId);
    }
}