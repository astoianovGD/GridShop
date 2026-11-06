package com.bobocode.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Kafka consumer in product-service that listens for {@link OrderCreatedEvent}
 * messages from order-service to track how many times each product was ordered.
 */
@Slf4j
@Component
public class OrderEventConsumer {

    private final Map<Long, LongAdder> productOrderCounts = new ConcurrentHashMap<>();

    /**
     * Consumes {@link OrderCreatedEvent} from Kafka and increments order counters per product.
     *
     * @param event the incoming order creation event
     */
    @KafkaListener(
            topics = KafkaConfig.ORDER_CREATED_TOPIC,
            groupId = "product-service-group",
            properties = {
                    "spring.json.value.default.type=com.bobocode.messaging.OrderCreatedEvent",
                    "spring.json.trusted.packages=*"
            }
    )
    public void handleOrderCreatedEvent(final OrderCreatedEvent event) {
        if (event == null || event.getProductIds() == null) {
            return;
        }
        log.info("Received OrderCreatedEvent in product-service: orderId={}, userId={}, products={}",
                event.getOrderId(), event.getUserId(), event.getProductIds());

        for (Long productId : event.getProductIds()) {
            productOrderCounts.computeIfAbsent(productId, id -> new LongAdder()).increment();
        }
    }

    /**
     * Returns the number of times a given product has been ordered according to consumed events.
     *
     * @param productId the product ID
     * @return total order count for the product
     */
    public long getOrderCountForProduct(final Long productId) {
        LongAdder adder = productOrderCounts.get(productId);
        return adder != null ? adder.sum() : 0L;
    }
}
