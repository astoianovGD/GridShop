package com.bobocode.dto.orders;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private long id;
    private LocalDateTime purchaseDate;
    private List<OrderItemDto> items;
}