package com.bobocode.Entities.Products;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the marketplace containing all available products.
 */
@Data
public class MarketPlace {

    /**
     * Default price for the T-shirt product.
     */
    private static final int T_SHIRT_PRICE = 20;

    /**
     * Default price for the Keychain product.
     */
    private static final int KEYCHAIN_PRICE = 1;

    /**
     * Map storing marketplace products by their ID.
     */
    private final Map<Long, Product> marketProducts = new HashMap<>();

    /**
     * Constructs a new MarketPlace and initializes it with default products.
     */
    public MarketPlace() {
        marketProducts.put(1L, new Product(1, "T-shirt Grid Dynamics",
                BigDecimal.valueOf(T_SHIRT_PRICE)));
        marketProducts.put(2L, new Product(2, "Grid Dynamics Keychain",
                BigDecimal.valueOf(KEYCHAIN_PRICE)));
    }
}

