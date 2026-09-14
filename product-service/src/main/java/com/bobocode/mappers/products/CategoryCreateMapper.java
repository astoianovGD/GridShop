package com.bobocode.mappers.products;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.entities.products.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting category creation DTOs to category entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryCreateMapper {

    /**
     * Converts a category creation DTO to a category entity.
     *
     * @param createDto the category creation DTO
     * @return the corresponding category entity
     */
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryCreateDto createDto);
}
