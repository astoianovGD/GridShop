package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.entities.orders.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting order entities to order DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = OrderItemMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    /**
     * Converts an order entity to an order DTO.
     *
     * @param order the order entity
     * @return the corresponding order DTO
     */
    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order);
}
