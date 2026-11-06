package com.bobocode.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer in order-service that publishes {@link OrderCreatedEvent}
 * when a customer places a new order.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaOperations<String, OrderCreatedEvent> kafkaTemplate;

    /**
     * Synchronously sends an {@link OrderCreatedEvent} to Kafka.
     *
     * @param event the order created event
     */
    public void sendOrderCreatedEvent(final OrderCreatedEvent event) {
        String key = String.valueOf(event.getOrderId());
        log.info("Publishing OrderCreatedEvent to topic '{}': orderId={}, userId={}, products={}",
                KafkaConfig.ORDER_CREATED_TOPIC, event.getOrderId(), event.getUserId(), event.getProductIds());
        kafkaTemplate.send(KafkaConfig.ORDER_CREATED_TOPIC, key, event);
    }

    /**
     * Asynchronously publishes an {@link OrderCreatedEvent} to Kafka so that
     * the order transaction is not blocked if the broker is temporarily unreachable.
     *
     * @param orderId    ID of the newly saved order
     * @param userId     ID of the user who placed the order
     * @param productIds list of product IDs included in the order
     */
    public void publishOrderCreatedAsync(
            final Long orderId, final Long userId, final List<Long> productIds
    ) {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, productIds);
        CompletableFuture.runAsync(() -> {
            try {
                sendOrderCreatedEvent(event);
            } catch (Exception ex) {
                log.warn("Could not publish OrderCreatedEvent for orderId={} (Kafka broker may be offline): {}",
                        orderId, ex.getMessage());
            }
        });
    }
}
