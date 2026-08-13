package com.bobocode.services.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for rendering the user's shopping bucket in the console.
 */
@Service
public class BucketConsoleViewService {

    /** Maximum allowed length for product names. */
    private static final int MAX_PRODUCT_NAME_LENGTH = 35;

    /** Maximum allowed length for category names. */
    private static final int MAX_CATEGORY_LENGTH = 15;

    /** Offset used when truncating strings to make room for ellipsis. */
    private static final int SUBSTRING_OFFSET = 3;

    /**
     * Displays the contents of the user's shopping bucket.
     *
     * @param bucketItems the list of bucket items to display
     */
    public void displayBucket(final List<BucketItemDto> bucketItems) {
        if (bucketItems == null || bucketItems.isEmpty()) {
            System.out.println("Your bucket is empty.");
            return;
        }

        System.out.println("\n========================================"
                + "====================================");
        System.out.printf("%-5s | %-35s | %-15s | %-10s | %-5s%n",
                "ID", "Product Name", "Category", "Price", "Qty");
        System.out.println("----------------------------------------"
                + "---------------------------------------------");

        bucketItems.forEach(item -> {
            String productName = formatLength(
                    item.getName(), MAX_PRODUCT_NAME_LENGTH
            );
            String categoryName = formatLength(
                    item.getCategoryName(), MAX_CATEGORY_LENGTH
            );

            System.out.printf("%-5d | %-35s | %-15s | $%9.2f | %-5d%n",
                    item.getProductId(),
                    productName,
                    categoryName,
                    item.getPrice(),
                    item.getQuantity());
        });

        System.out.println("----------------------------------------"
                + "---------------------------------------------");
    }

    /**
     * Helper method to truncate long strings with '...'
     * if they exceed max length.
     *
     * @param text      the text to format
     * @param maxLength the maximum allowed length
     * @return the formatted text string
     */
    private String formatLength(final String text, final int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - SUBSTRING_OFFSET) + "...";
    }
}
