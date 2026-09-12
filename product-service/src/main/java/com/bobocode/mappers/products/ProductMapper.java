package com.bobocode.mappers.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between product entities and product DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    /**
     * Converts a product DTO to a product entity.
     *
     * @param productDto the product DTO
     * @return the corresponding product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductDto productDto);

    /**
     * Converts a product entity to a product DTO.
     *
     * @param product the product entity
     * @return the corresponding product DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);
}
