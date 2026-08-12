package com.bobocode.dto.bucket;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BucketItemDto {
    private long productId;
    private String name;
    private BigDecimal price;
    private String categoryName;
    private int quantity;
}
