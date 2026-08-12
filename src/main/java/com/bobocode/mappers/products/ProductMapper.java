package com.bobocode.mappers.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);
}
