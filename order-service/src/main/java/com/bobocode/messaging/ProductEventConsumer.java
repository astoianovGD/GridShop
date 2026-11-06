package com.bobocode.messaging;

import com.bobocode.services.bucket.BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer in order-service that listens for {@link ProductRemovedEvent}
 * published by product-service and removes deactivated products from all user buckets.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final BucketService bucketService;

    /**
     * Handles {@link ProductRemovedEvent} messages from Kafka by deleting the removed
     * product from all users' shopping buckets.
     *
     * @param event the product removal event
     */
    @KafkaListener(
            topics = KafkaConfig.PRODUCT_REMOVED_TOPIC,
            groupId = "order-service-group",
            properties = {
                    "spring.json.value.default.type=com.bobocode.messaging.ProductRemovedEvent",
                    "spring.json.trusted.packages=*"
            }
    )
    public void handleProductRemovedEvent(final ProductRemovedEvent event) {
        if (event == null || event.getProductId() == null) {
            return;
        }
        log.info("Received ProductRemovedEvent in order-service: removing productId={} ('{}') from all user buckets",
                event.getProductId(), event.getProductName());
        bucketService.removeProductFromAllBuckets(event.getProductId());
    }
}
