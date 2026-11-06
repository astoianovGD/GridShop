package com.bobocode.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer in product-service that publishes {@link ProductRemovedEvent}
 * whenever a product is removed/deactivated in the marketplace.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaOperations<String, ProductRemovedEvent> kafkaTemplate;

    /**
     * Synchronously dispatches a {@link ProductRemovedEvent} to Kafka.
     *
     * @param event the product removal event
     */
    public void sendProductRemovedEvent(final ProductRemovedEvent event) {
        String key = String.valueOf(event.getProductId());
        log.info("Publishing ProductRemovedEvent to topic '{}': productId={}, name='{}'",
                KafkaConfig.PRODUCT_REMOVED_TOPIC, event.getProductId(), event.getProductName());
        kafkaTemplate.send(KafkaConfig.PRODUCT_REMOVED_TOPIC, key, event);
    }

    /**
     * Asynchronously publishes a {@link ProductRemovedEvent} to Kafka so that
     * the caller transaction is not blocked if the broker is temporarily unreachable.
     *
     * @param productId   ID of the deactivated product
     * @param productName name of the deactivated product
     */
    public void publishProductRemovedAsync(final Long productId, final String productName) {
        ProductRemovedEvent event = new ProductRemovedEvent(
                productId,
                productName,
                System.currentTimeMillis()
        );
        CompletableFuture.runAsync(() -> {
            try {
                sendProductRemovedEvent(event);
            } catch (Exception ex) {
                log.warn("Could not publish ProductRemovedEvent for productId={} (Kafka broker may be offline): {}",
                        productId, ex.getMessage());
            }
        });
    }
}
