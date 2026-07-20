package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MarketPlaceService {

    private final MarketPlace marketPlace;
    private long productIdCounter = 3;

    public void addNewProduct(Product product){
        product.setId(productIdCounter++);
        marketPlace.getMarketProducts().put(product.getId(), product);
    }

    public void removeProduct(long id) {
        Product removedProduct = marketPlace.getMarketProducts().remove(id);

        if (removedProduct == null) {
            throw new EntityNotFoundException("Product with ID " + id + " not found!");
        }
    }

    public void editProduct(Product product) {
        marketPlace.getMarketProducts().put(product.getId(), product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(marketPlace.getMarketProducts().values());
    }

    public Product getProductById(long id) {
        Product product = marketPlace.getMarketProducts().get(id);
        if (product != null) {
            return product;
        }
        throw new EntityNotFoundException("Product with ID " + id + " not found!");
    }
}
