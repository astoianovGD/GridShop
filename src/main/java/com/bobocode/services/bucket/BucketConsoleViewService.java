package com.bobocode.services.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BucketConsoleViewService {

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

        System.out.println("\n============================================ YOUR BUCKET ============================================");
        System.out.printf("%-5s | %-35s | %-15s | %-10s | %-5s%n", "ID", "Product Name", "Category", "Price", "Qty");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        bucketItems.forEach(item -> {
            String productName = formatLength(item.getName(), 35);
            String categoryName = formatLength(item.getCategoryName(), 15);

            System.out.printf("%-5d | %-35s | %-15s | $%9.2f | %-5d%n",
                    item.getProductId(),
                    productName,
                    categoryName,
                    item.getPrice(),
                    item.getQuantity());
        });

        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    /**
     * Helper method to truncate long strings with '...' if they exceed max length.
     */
    private String formatLength(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

}
