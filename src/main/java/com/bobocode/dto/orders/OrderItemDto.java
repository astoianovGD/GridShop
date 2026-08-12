package com.bobocode.dto.orders;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private long orderItemId;
    private long productId;
    private String productName;
    private BigDecimal priceAtPurchase;
    private int quantity;
}
