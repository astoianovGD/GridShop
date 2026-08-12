package com.bobocode.services.products;

import com.bobocode.dto.products.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsConsoleViewService {

    /**
     * Displays the list of products in the catalog.
     *
     * @param products the list of products to display
     */
    public void catalogAllProducts(final List<ProductDto> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("Marketplace is empty right now.");
            return;
        }

        System.out.println("--- Available Products ---");
        products.forEach(product ->
                System.out.printf("ID: %-3d | Name: %-20s | Price: $%6.2f%n",
                        product.getId(),
                        product.getName(),
                        product.getPrice())
        );
        System.out.println("--------------------------");
    }



}
