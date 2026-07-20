package com.bobocode.Entities.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Services.Products.FilterProductsService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.SortProductsService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class CatalogMenu {
    private final MarketPlaceService marketPlaceService;
    private final FilterProductsService filterProductsService;
    private final SortProductsService sortProductsService;

    public void handleOptions(String option, Scanner scanner) {
        switch (option) {
            case "1" -> {
                System.out.println("Choose sorting:");
                System.out.println("1) Price Asc \n2) Price Desc \n3) Name Asc \n4) Name Desc");
                String sortOption = scanner.nextLine();

                List<Product> sorted = switch (sortOption) {
                    case "1" -> sortProductsService.sortProductsByPriceAsc(marketPlaceService.getAllProducts());
                    case "2" -> sortProductsService.sortProductsByPriceDesc(marketPlaceService.getAllProducts());
                    case "3" -> sortProductsService.sortProductsByNameAsc(marketPlaceService.getAllProducts());
                    case "4" -> sortProductsService.sortProductsByNameDesc(marketPlaceService.getAllProducts());
                    default -> {
                        System.out.println("Invalid sorting option.");
                        //400
                        yield List.of();
                    }
                };
                if (sorted.isEmpty()) System.out.println("No products found.");
                else catalogAllProducts(sorted);
            }
            case "2" -> {
                System.out.println("Choose filter:");
                System.out.println("1) Starts with letter \n2) Price higher than \n3) Price lower than");
                String filterOption = scanner.nextLine();

                List<Product> filtered = switch (filterOption) {
                    case "1" -> {
                        System.out.println("Enter letter:");
                        String input = scanner.nextLine();
                        if (input.isEmpty()) {
                            System.out.println("Input cannot be empty.");
                            yield List.of();
                        }
                        yield filterProductsService.filterProductsByLetterBeginWith(input.charAt(0), marketPlaceService.getAllProducts());
                    }
                    case "2" -> {
                        System.out.println("Enter minimum price:");
                        try {
                            BigDecimal minPrice = new BigDecimal(scanner.nextLine());
                            yield filterProductsService.filterProductsByPriceHigherThan(minPrice, marketPlaceService.getAllProducts());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid price format! Please enter a valid number.");
                            yield List.of();
                        }
                    }
                    case "3" -> {
                        System.out.println("Enter maximum price:");
                        try {
                            BigDecimal maxPrice = new BigDecimal(scanner.nextLine());
                            yield filterProductsService.filterProductsByPriceLowerThan(maxPrice, marketPlaceService.getAllProducts());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid price format! Please enter a valid number.");
                            yield List.of();
                        }
                    }
                    default -> {
                        System.out.println("Invalid filter option.");
                        yield List.of();
                    }
                };
                if (filtered.isEmpty()) System.out.println("No products match your filter.");
                else catalogAllProducts(filtered);
            }
            case "3" -> {
                System.out.println("Enter search keyword:");
                String keyword = scanner.nextLine();
                List<Product> searched = filterProductsService.filterProductsByName(keyword, marketPlaceService.getAllProducts());
                if (searched.isEmpty()) System.out.println("No products match your keyword.");
                else catalogAllProducts(searched);
            }
        }
    }

    public void catalogAllProducts(List<Product> products) {

        if (products.isEmpty()) {
            System.out.println("Marketplace is empty right now.");
        } else {
            System.out.println("--- Available Products ---");
            products.forEach(product -> {
                System.out.println("ID: " + product.getId() + " | Name: " + product.getName() + " | Price: $" + product.getPrice());
            });
            System.out.println("--------------------------");
        }
    }
}