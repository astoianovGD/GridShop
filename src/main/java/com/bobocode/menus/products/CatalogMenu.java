package com.bobocode.menus.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.products.filtering.FilterProductService;
import com.bobocode.services.products.sorting.SortProductService;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Menu for browsing, sorting, and filtering the product catalog.
 */
@RequiredArgsConstructor
@Service
public final class CatalogMenu {

    @NonNull
    private final ProductsConsoleViewService productsConsoleViewService;

    @NonNull
    private final SortProductService sortProductService;

    @NonNull
    private final FilterProductService filterProductService;

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

        List<ProductDto> sorted = switch (sortOption) {
            case "1" -> sortProductService.filterByPriceAsc();
            case "2" -> sortProductService.filterByProductDesc();
            case "3" -> sortProductService.filterByNameAsc();
            case "4" -> sortProductService.filterByNameDesc();
            default -> {
                System.out.println("Invalid sorting option.");
                yield List.of();
            }
        };

        if (sorted.isEmpty()) {
            System.out.println("No products found.");
        } else {
            productsConsoleViewService.catalogAllProducts(sorted);
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

        List<ProductDto> filtered = switch (filterOption) {
            case "1" -> {
                System.out.println("Enter letter:");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty.");
                    yield List.of();
                }
                yield filterProductService.filterByNameStartingWith(input);
            }
            case "2" -> {
                BigDecimal minPrice = InputValidator.getValidPrice(
                        scanner, "Enter minimum price:"
                );
                yield filterProductService.filterByPriceGreaterThan(minPrice);
            }
            case "3" -> {
                BigDecimal maxPrice = InputValidator.getValidPrice(
                        scanner, "Enter maximum price:"
                );
                yield filterProductService.filterByPriceLowerThan(maxPrice);
            }
            default -> {
                System.out.println("Invalid filter option.");
                yield List.of();
            }
        };

        if (filtered.isEmpty()) {
            System.out.println("No products match your filter.");
        } else {
            productsConsoleViewService.catalogAllProducts(filtered);
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
        List<ProductDto> searched = filterProductService.filterByNameContains(keyword);

        if (searched.isEmpty()) {
            System.out.println("No products match your keyword.");
        } else {
            productsConsoleViewService.catalogAllProducts(searched);
        }
    }
}