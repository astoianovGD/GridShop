package com.bobocode.services.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BucketConsoleViewServiceTest {

    private final BucketConsoleViewService bucketConsoleViewService = new BucketConsoleViewService();

    @Test
    void shouldDisplayEmptyBucketMessageWhenListIsNull() {
        assertDoesNotThrow(() -> bucketConsoleViewService.displayBucket(null));
    }

    @Test
    void shouldDisplayEmptyBucketMessageWhenListIsEmpty() {
        assertDoesNotThrow(() -> bucketConsoleViewService.displayBucket(Collections.emptyList()));
    }

    @Test
    void shouldDisplayBucketItemsSuccessfully() {
        BucketItemDto item1 = new BucketItemDto();
        item1.setProductId(1L);
        item1.setName("Short Name");
        item1.setCategoryName("Electronics");
        item1.setPrice(new BigDecimal("99.99"));
        item1.setQuantity(2);

        BucketItemDto item2 = new BucketItemDto();
        item2.setProductId(2L);
        item2.setName("This is a very long product name that exceeds the maximum allowed length of thirty five characters");
        item2.setCategoryName("This is a very long category name");
        item2.setPrice(new BigDecimal("1250.50"));
        item2.setQuantity(1);

        List<BucketItemDto> items = List.of(item1, item2);

        assertDoesNotThrow(() -> bucketConsoleViewService.displayBucket(items));
    }
}