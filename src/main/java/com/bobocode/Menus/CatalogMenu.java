package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Services.Products.FilterProductsService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.SortProductsService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Menu for browsing, sorting, and filtering the product catalog.
 */
@RequiredArgsConstructor
public final class CatalogMenu {

    /** Service for interacting with marketplace products. */
    private final MarketPlaceService marketPlaceService;

    /** Service for filtering products. */
    private final FilterProductsService filterProductsService;

    /** Service for sorting products. */
    private final SortProductsService sortProductsService;

    /**
     * Handles the selected catalog option.
     *
     * @param option  the selected menu option
     * @param scanner the scanner for reading user input
     */
    public void handleOptions(final String option, final Scanner scanner) {
        switch (option) {
            case "1" -> handleSorting(scanner);
            case "2" -> handleFiltering(scanner);
            case "3" -> handleSearching(scanner);
            default -> System.out.println("Invalid catalog option.");
        }
    }

    /**
     * Handles the sorting of products.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleSorting(final Scanner scanner) {
        System.out.println("Choose sorting:");
        System.out.println("1) Price Asc \n2) Price Desc \n"
                + "3) Name Asc \n4) Name Desc");
        String sortOption = scanner.nextLine();

        List<Product> sorted = switch (sortOption) {
            case "1" -> sortProductsService.sortProductsByPriceAsc(
                    marketPlaceService.getAllProducts());
            case "2" -> sortProductsService.sortProductsByPriceDesc(
                    marketPlaceService.getAllProducts());
            case "3" -> sortProductsService.sortProductsByNameAsc(
                    marketPlaceService.getAllProducts());
            case "4" -> sortProductsService.sortProductsByNameDesc(
                    marketPlaceService.getAllProducts());
            default -> {
                System.out.println("Invalid sorting option.");
                yield List.of();
            }
        };

        if (sorted.isEmpty()) {
            System.out.println("No products found.");
        } else {
            catalogAllProducts(sorted);
        }
    }

    /**
     * Handles the filtering of products.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleFiltering(final Scanner scanner) {
        System.out.println("Choose filter:");
        System.out.println("1) Starts with letter \n2) Price higher than \n"
                + "3) Price lower than");
        String filterOption = scanner.nextLine();

        List<Product> filtered = switch (filterOption) {
            case "1" -> {
                System.out.println("Enter letter:");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty.");
                    yield List.of();
                }
                yield filterProductsService.filterProductsByLetterBeginWith(
                        input.charAt(0),
                        marketPlaceService.getAllProducts());
            }
            case "2" -> {
                System.out.println("Enter minimum price:");
                try {
                    BigDecimal minPrice = new BigDecimal(scanner.nextLine());
                    yield filterProductsService.filterProductsByPriceHigherThan(
                            minPrice,
                            marketPlaceService.getAllProducts());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format! "
                            + "Please enter a valid number.");
                    yield List.of();
                }
            }
            case "3" -> {
                System.out.println("Enter maximum price:");
                try {
                    BigDecimal maxPrice = new BigDecimal(scanner.nextLine());
                    yield filterProductsService.filterProductsByPriceLowerThan(
                            maxPrice,
                            marketPlaceService.getAllProducts());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format! "
                            + "Please enter a valid number.");
                    yield List.of();
                }
            }
            default -> {
                System.out.println("Invalid filter option.");
                yield List.of();
            }
        };

        if (filtered.isEmpty()) {
            System.out.println("No products match your filter.");
        } else {
            catalogAllProducts(filtered);
        }
    }

    /**
     * Handles searching for products by keyword.
     *
     * @param scanner the scanner for reading user input
     */
    private void handleSearching(final Scanner scanner) {
        System.out.println("Enter search keyword:");
        String keyword = scanner.nextLine();
        List<Product> searched = filterProductsService.filterProductsByName(
                keyword,
                marketPlaceService.getAllProducts());

        if (searched.isEmpty()) {
            System.out.println("No products match your keyword.");
        } else {
            catalogAllProducts(searched);
        }
    }

    /**
     * Displays the list of products in the catalog.
     *
     * @param products the list of products to display
     */
    public void catalogAllProducts(final List<Product> products) {

        if (products.isEmpty()) {
            System.out.println("Marketplace is empty right now.");
        } else {
            System.out.println("--- Available Products ---");
            products.forEach(product -> {
                System.out.println("ID: " + product.getId()
                        + " | Name: " + product.getName()
                        + " | Price: $" + product.getPrice());
            });
            System.out.println("--------------------------");
        }
    }
}
