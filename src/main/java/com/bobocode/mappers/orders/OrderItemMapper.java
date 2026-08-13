package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderItemDto;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.bucket.BucketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between order items and related data transfer objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    /**
     * Converts a bucket item and order into an order item entity.
     *
     * @param bucketItem the bucket item entity
     * @param order the order entity
     * @return the created order item entity
     */
    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "order", source = "order")
    @Mapping(target = "product", source = "bucketItem.product")
    @Mapping(target = "priceAtPurchase", source = "bucketItem.product.price")
    @Mapping(target = "quantity", source = "bucketItem.quantity")
    OrderItem toOrderItem(BucketItem bucketItem, Order order);

    /**
     * Converts an order item entity to an order item DTO.
     *
     * @param orderItem the order item entity
     * @return the corresponding order item DTO
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDto toDto(OrderItem orderItem);
}
