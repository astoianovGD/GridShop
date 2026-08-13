package com.bobocode.mappers.products;

import com.bobocode.dto.products.CategoryDto;
import com.bobocode.entities.products.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between category entities and category DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    /**
     * Converts a category DTO to a category entity.
     *
     * @param categoryDto the category DTO
     * @return the corresponding category entity
     */
    Category toEntity(CategoryDto categoryDto);

    /**
     * Converts a category entity to a category DTO.
     *
     * @param category the category entity
     * @return the corresponding category DTO
     */
    CategoryDto toDto(Category category);
}
