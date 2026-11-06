package com.bobocode.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductMessagingTest {

    @Mock
    private KafkaOperations<String, ProductRemovedEvent> kafkaTemplate;

    @InjectMocks
    private ProductEventProducer productEventProducer;

    @Test
    @DisplayName("ProductEventProducer should send ProductRemovedEvent to product-removed-topic")
    void shouldSendProductRemovedEventToKafka() {
        ProductRemovedEvent event = new ProductRemovedEvent(42L, "Mechanical Keyboard", 1700000000000L);

        productEventProducer.sendProductRemovedEvent(event);

        verify(kafkaTemplate).send(KafkaConfig.PRODUCT_REMOVED_TOPIC, "42", event);
    }

    @Test
    @DisplayName("OrderEventConsumer should track product order counts from OrderCreatedEvent")
    void shouldConsumeOrderCreatedEventAndTrackPopularity() {
        OrderEventConsumer consumer = new OrderEventConsumer();

        OrderCreatedEvent event1 = new OrderCreatedEvent(100L, 1L, List.of(10L, 20L));
        OrderCreatedEvent event2 = new OrderCreatedEvent(101L, 2L, List.of(10L, 30L));

        consumer.handleOrderCreatedEvent(event1);
        consumer.handleOrderCreatedEvent(event2);

        assertEquals(2L, consumer.getOrderCountForProduct(10L));
        assertEquals(1L, consumer.getOrderCountForProduct(20L));
        assertEquals(1L, consumer.getOrderCountForProduct(30L));
        assertEquals(0L, consumer.getOrderCountForProduct(99L));
    }
}
