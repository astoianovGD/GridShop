package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class FilterProductsService {

    public List <Product> filterProductsByLetterBeginWith(char letter, List<Product> productList) {
        String searchPrefix = String.valueOf(letter).toLowerCase();
        return productList.stream()
                .filter(x -> x.getName().toLowerCase().startsWith(searchPrefix))
                .toList();
    }

    public List <Product> filterProductsByPriceHigherThan(BigDecimal price, List<Product> productList) {
        return productList.stream()
                .filter(x -> x.getPrice().compareTo(price) >= 0)
                .toList();
    }

    public List <Product> filterProductsByPriceLowerThan(BigDecimal price, List<Product> productList) {
        return productList.stream()
                .filter(x -> x.getPrice().compareTo(price) <= 0)
                .toList();
    }

    public List<Product> filterProductsByName(String keyword,List<Product> productList) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productList;
        }

        String lowerCaseKeyword = keyword.toLowerCase();

        return productList.stream()
                .filter(x -> x.getName().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }
}
