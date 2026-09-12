package com.bobocode.mappers.bucket;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.bucket.BucketItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting {@link BucketItem} entities to {@link BucketItemDto} objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketItemMapper {

    /**
     * Converts a bucket item entity and product DTO (from product-service via Feign) to a bucket item DTO.
     *
     * @param bucketItem the bucket item entity
     * @param product the product DTO
     * @return the corresponding bucket item DTO
     */
    @Mapping(target = "productId", source = "bucketItem.productId")
    @Mapping(target = "quantity", source = "bucketItem.quantity")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "categoryName", source = "product.categoryName")
    BucketItemDto toDto(BucketItem bucketItem, ProductDto product);

    /**
     * Converts a bucket item entity to a bucket item DTO without extra product info.
     *
     * @param bucketItem the bucket item entity
     * @return bucket item DTO
     */
    default BucketItemDto toDto(BucketItem bucketItem) {
        if (bucketItem == null) {
            return null;
        }
        BucketItemDto dto = new BucketItemDto();
        dto.setProductId(bucketItem.getProductId());
        dto.setQuantity(bucketItem.getQuantity());
        return dto;
    }
}
