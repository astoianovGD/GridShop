package com.bobocode.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for topics used by product-service.
 */
@Configuration
public class KafkaConfig {

    /**
     * Topic name for product removal events.
     */
    public static final String PRODUCT_REMOVED_TOPIC = "product-removed-topic";

    /**
     * Topic name for order creation events.
     */
    public static final String ORDER_CREATED_TOPIC = "order-created-topic";

    /**
     * Defines the Kafka topic for product removal notifications.
     *
     * @return the configured {@link NewTopic}
     */
    @Bean
    public NewTopic productRemovedTopic() {
        return TopicBuilder.name(PRODUCT_REMOVED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Defines the Kafka topic for order creation events.
     *
     * @return the configured {@link NewTopic}
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
