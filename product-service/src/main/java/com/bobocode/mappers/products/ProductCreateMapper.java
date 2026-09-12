package com.bobocode.mappers.products;

import com.bobocode.entities.products.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting product creation DTOs to product entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductCreateMapper {

    /**
     * Converts a product creation DTO to a product entity.
     *
     * @param createDto the product creation DTO
     * @return the corresponding product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(com.bobocode.dto.products.ProductCreateDto createDto);
}
