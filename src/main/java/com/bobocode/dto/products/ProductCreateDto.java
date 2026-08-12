package com.bobocode.dto.products;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateDto {
    private String name;
    private BigDecimal price;
    private long categoryId;
}