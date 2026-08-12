package com.bobocode.dto.products;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {
    private long id;
    private String name;
    private BigDecimal price;
    private String categoryName;
    private boolean isActive;
}