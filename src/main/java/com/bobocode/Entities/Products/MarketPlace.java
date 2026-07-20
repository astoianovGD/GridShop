package com.bobocode.Entities.Products;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class MarketPlace {
    private final Map<Long, Product> marketProducts = new HashMap<>();

    public MarketPlace() {
        marketProducts.put(1L, new Product(1,"T-shirt Grid Dynamics", BigDecimal.valueOf(20)));
        marketProducts.put(2L, new Product(2, "Grid Dynamics Keychain", BigDecimal.valueOf(1)));
    }
}
