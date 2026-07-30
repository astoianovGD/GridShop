package com.bobocode.Entities.Products;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the marketplace containing all available products.
 */
@Data
public class MarketPlace {

    /**
     * Map storing marketplace products by their ID.
     */
    private final Map<Long, Product> marketProducts = new HashMap<>();
}

