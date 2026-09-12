package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderItemDto;
import com.bobocode.entities.orders.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between order items and related data transfer objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    /**
     * Converts an order item entity to an order item DTO.
     *
     * @param orderItem the order item entity
     * @return the corresponding order item DTO
     */
    OrderItemDto toDto(OrderItem orderItem);
}
