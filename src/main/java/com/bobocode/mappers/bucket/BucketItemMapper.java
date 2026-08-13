package com.bobocode.mappers.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.entities.bucket.BucketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting {@link BucketItem}
 * entities to {@link BucketItemDto} objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketItemMapper {

    /**
     * Converts a bucket item entity to a bucket item DTO.
     *
     * @param bucketItem the bucket item entity
     * @return the corresponding bucket item DTO
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "quantity", source = "quantity")
    BucketItemDto toDto(BucketItem bucketItem);
}
