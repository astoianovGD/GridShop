package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderItemDto;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.bucket.BucketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "order", source = "order")
    @Mapping(target = "product", source = "bucketItem.product")
    @Mapping(target = "priceAtPurchase", source = "bucketItem.product.price")
    @Mapping(target = "quantity", source = "bucketItem.quantity")
    OrderItem toOrderItem(BucketItem bucketItem, Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDto toDto(OrderItem orderItem);
}