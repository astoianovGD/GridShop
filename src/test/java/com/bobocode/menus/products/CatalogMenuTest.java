package com.bobocode.menus.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.products.filtering.FilterProductService;
import com.bobocode.services.products.sorting.SortProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogMenuTest {

    @Mock
    private ProductsConsoleViewService productsConsoleViewService;

    @Mock
    private SortProductService sortProductService;

    @Mock
    private FilterProductService filterProductService;

    @InjectMocks
    private CatalogMenu catalogMenu;

    @Test
    void shouldHandleSortingPriceAscSuccessfully() {
        ProductDto product = new ProductDto();
        when(sortProductService.filterByPriceAsc()).thenReturn(List.of(product));

        // Input for sorting menu: "1" (Price Asc)
        Scanner scanner = createScanner("1\n");

        catalogMenu.handleOptions("1", scanner);

        verify(sortProductService).filterByPriceAsc();
        verify(productsConsoleViewService).catalogAllProducts(anyList());
    }

    @Test
    void shouldHandleSortingEmptyResult() {
        when(sortProductService.filterByProductDesc()).thenReturn(Collections.emptyList());

        // Input for sorting menu: "2" (Price Desc)
        Scanner scanner = createScanner("2\n");

        catalogMenu.handleOptions("1", scanner);

        verify(sortProductService).filterByProductDesc();
        verifyNoInteractions(productsConsoleViewService);
    }

    @Test
    void shouldHandleInvalidSortingOption() {
        // Input for sorting menu: "invalid"
        Scanner scanner = createScanner("invalid\n");

        catalogMenu.handleOptions("1", scanner);

        verifyNoInteractions(sortProductService, productsConsoleViewService);
    }

    @Test
    void shouldHandleFilteringStartsWithLetter() {
        ProductDto product = new ProductDto();
        when(filterProductService.filterByNameStartingWith("A")).thenReturn(List.of(product));

        // Input for filtering menu: "1" (Starts with letter), then "A"
        Scanner scanner = createScanner("1\nA\n");

        catalogMenu.handleOptions("2", scanner);

        verify(filterProductService).filterByNameStartingWith("A");
        verify(productsConsoleViewService).catalogAllProducts(anyList());
    }

    @Test
    void shouldHandleFilteringStartsWithEmptyInput() {
        // Input for filtering menu: "1", then empty string
        Scanner scanner = createScanner("1\n\n");

        catalogMenu.handleOptions("2", scanner);

        verifyNoInteractions(filterProductService, productsConsoleViewService);
    }

    @Test
    void shouldHandleFilteringPriceHigherThan() {
        ProductDto product = new ProductDto();
        when(filterProductService.filterByPriceGreaterThan(new BigDecimal("50.00"))).thenReturn(List.of(product));

        // Input for filtering menu: "2" (Price higher than), then "50.00"
        Scanner scanner = createScanner("2\n50.00\n");

        catalogMenu.handleOptions("2", scanner);

        verify(filterProductService).filterByPriceGreaterThan(new BigDecimal("50.00"));
        verify(productsConsoleViewService).catalogAllProducts(anyList());
    }

    @Test
    void shouldHandleFilteringPriceLowerThan() {
        when(filterProductService.filterByPriceLowerThan(new BigDecimal("100.00"))).thenReturn(Collections.emptyList());

        // Input for filtering menu: "3" (Price lower than), then "100.00"
        Scanner scanner = createScanner("3\n100.00\n");

        catalogMenu.handleOptions("2", scanner);

        verify(filterProductService).filterByPriceLowerThan(new BigDecimal("100.00"));
        verifyNoInteractions(productsConsoleViewService);
    }

    @Test
    void shouldHandleInvalidFilteringOption() {
        // Input for filtering menu: "invalid"
        Scanner scanner = createScanner("invalid\n");

        catalogMenu.handleOptions("2", scanner);

        verifyNoInteractions(filterProductService, productsConsoleViewService);
    }

    @Test
    void shouldHandleSearchingKeywordSuccessfully() {
        ProductDto product = new ProductDto();
        when(filterProductService.filterByNameContains("Phone")).thenReturn(List.of(product));

        // Input for searching: "Phone"
        Scanner scanner = createScanner("Phone\n");

        catalogMenu.handleOptions("3", scanner);

        verify(filterProductService).filterByNameContains("Phone");
        verify(productsConsoleViewService).catalogAllProducts(anyList());
    }

    @Test
    void shouldHandleSearchingKeywordEmptyResult() {
        when(filterProductService.filterByNameContains("Unknown")).thenReturn(Collections.emptyList());

        // Input for searching: "Unknown"
        Scanner scanner = createScanner("Unknown\n");

        catalogMenu.handleOptions("3", scanner);

        verify(filterProductService).filterByNameContains("Unknown");
        verifyNoInteractions(productsConsoleViewService);
    }

    @Test
    void shouldHandleDefaultInvalidCatalogOption() {
        Scanner scanner = createScanner("");

        catalogMenu.handleOptions("99", scanner);

        verifyNoInteractions(sortProductService, filterProductService, productsConsoleViewService);
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}