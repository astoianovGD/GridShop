package com.bobocode.mappers.products;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.entities.products.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryCreateMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryCreateDto createDto);
}
