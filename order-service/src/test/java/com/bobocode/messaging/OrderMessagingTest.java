package com.bobocode.messaging;

import com.bobocode.clients.product.ProductClient;
import com.bobocode.mappers.bucket.BucketItemMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.services.bucket.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderMessagingTest {

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private BucketItemRepository bucketItemRepository;

    @Mock
    private BucketItemMapper bucketItemMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private KafkaOperations<String, OrderCreatedEvent> kafkaTemplate;

    private ProductEventConsumer productEventConsumer;
    private OrderEventProducer orderEventProducer;

    @BeforeEach
    void setUp() {
        BucketService bucketService = new BucketService(
                bucketRepository,
                bucketItemRepository,
                bucketItemMapper,
                productClient
        );
        productEventConsumer = new ProductEventConsumer(bucketService);
        orderEventProducer = new OrderEventProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("ProductEventConsumer should remove deactivated product from all user buckets")
    void shouldRemoveDeactivatedProductFromAllBucketsOnKafkaEvent() {
        ProductRemovedEvent event = new ProductRemovedEvent(42L, "Mechanical Keyboard", 1700000000000L);

        productEventConsumer.handleProductRemovedEvent(event);

        verify(bucketItemRepository).deleteAllByProductId(42L);
    }

    @Test
    @DisplayName("OrderEventProducer should send OrderCreatedEvent to order-created-topic")
    void shouldSendOrderCreatedEventToKafka() {
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 1L, List.of(10L, 20L));

        orderEventProducer.sendOrderCreatedEvent(event);

        verify(kafkaTemplate).send(KafkaConfig.ORDER_CREATED_TOPIC, "100", event);
    }
}
