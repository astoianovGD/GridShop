package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing products within the marketplace.
 */
@RequiredArgsConstructor
public final class MarketPlaceService {

    /** The starting ID for new products. */
    private static final long INITIAL_PRODUCT_ID = 3L;

    /** The marketplace entity containing the products. */
    @NonNull
    private final MarketPlace marketPlace;

    /** Counter for assigning unique product IDs. */
    private long productIdCounter = INITIAL_PRODUCT_ID;

    /**
     * Adds a new product to the marketplace.
     *
     * @param product the product to be added
     */
    public void addNewProduct(final Product product) {
        product.setId(productIdCounter++);
        marketPlace.getMarketProducts().put(product.getId(), product);
    }

    /**
     * Removes a product from the marketplace by its ID.
     *
     * @param id the ID of the product to remove
     * @throws EntityNotFoundException if the product is not found
     */
    public void removeProduct(final long id) {
        Product removedProduct = marketPlace.getMarketProducts().remove(id);

        if (removedProduct == null) {
            throw new EntityNotFoundException("Product with ID " + id
                    + " not found!");
        }
    }

    /**
     * Edits an existing product in the marketplace.
     *
     * @param product the product with updated information
     */
    public void editProduct(final Product product) {
        marketPlace.getMarketProducts().put(product.getId(), product);
    }

    /**
     * Retrieves a list of all products in the marketplace.
     *
     * @return a list containing all products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(marketPlace.getMarketProducts().values());
    }

    /**
     * Retrieves a product from the marketplace by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return the requested product
     * @throws EntityNotFoundException if the product is not found
     */
    public Product getProductById(final long id) {
        Product product = marketPlace.getMarketProducts().get(id);
        if (product != null) {
            return product;
        }
        throw new EntityNotFoundException("Product with ID " + id
                + " not found!");
    }
}
