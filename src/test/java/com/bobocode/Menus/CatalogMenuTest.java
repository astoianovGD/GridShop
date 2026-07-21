package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Services.Products.FilterProductsService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.SortProductsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogMenuTest {

    private MarketPlaceService marketPlaceServiceMock;
    private FilterProductsService filterProductsServiceMock;
    private SortProductsService sortProductsServiceMock;
    private CatalogMenu catalogMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        filterProductsServiceMock = mock(FilterProductsService.class);
        sortProductsServiceMock = mock(SortProductsService.class);

        catalogMenu = new CatalogMenu(marketPlaceServiceMock, filterProductsServiceMock, sortProductsServiceMock);

        // Intercept console output
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if any dependency is null upon creation")
    void testNullDependencies() {
        assertThrows(NullPointerException.class, () -> new CatalogMenu(null, filterProductsServiceMock, sortProductsServiceMock));
        assertThrows(NullPointerException.class, () -> new CatalogMenu(marketPlaceServiceMock, null, sortProductsServiceMock));
        assertThrows(NullPointerException.class, () -> new CatalogMenu(marketPlaceServiceMock, filterProductsServiceMock, null));
    }

    @Test
    @DisplayName("Should print empty message when catalogAllProducts receives an empty list")
    void testCatalogAllProducts_Empty() {
        catalogMenu.catalogAllProducts(Collections.emptyList());
        assertTrue(outContent.toString().contains("Marketplace is empty right now."));
    }

    @Test
    @DisplayName("Should correctly print list of products when catalogAllProducts is called")
    void testCatalogAllProducts_Success() {
        Product p = new Product(1L, "Laptop", new BigDecimal("1200.00"));
        catalogMenu.catalogAllProducts(List.of(p));

        String output = outContent.toString();
        assertTrue(output.contains("--- Available Products ---"));
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Name: Laptop"));
        assertTrue(output.contains("Price: $1200.00"));
    }

    @Test
    @DisplayName("Should handle invalid option in handleOptions")
    void testHandleOptions_InvalidOption() {
        catalogMenu.handleOptions("99", new Scanner(""));
        assertTrue(outContent.toString().contains("Invalid catalog option."));
    }

    @Test
    @DisplayName("Should handle sorting by price ascending successfully")
    void testHandleSorting_PriceAsc() {
        Product p = new Product(1L, "Book", BigDecimal.TEN);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(p));
        when(sortProductsServiceMock.sortProductsByPriceAsc(anyList())).thenReturn(List.of(p));

        Scanner scanner = new Scanner("1\n");
        catalogMenu.handleOptions("1", scanner);

        verify(sortProductsServiceMock, times(1)).sortProductsByPriceAsc(anyList());
        assertTrue(outContent.toString().contains("Book"));
    }

    @Test
    @DisplayName("Should handle sorting by price descending")
    void testHandleSorting_PriceDesc() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of());
        Scanner scanner = new Scanner("2\n");
        catalogMenu.handleOptions("1", scanner);
        verify(sortProductsServiceMock, times(1)).sortProductsByPriceDesc(anyList());
    }

    @Test
    @DisplayName("Should handle sorting by name ascending")
    void testHandleSorting_NameAsc() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of());
        Scanner scanner = new Scanner("3\n");
        catalogMenu.handleOptions("1", scanner);
        verify(sortProductsServiceMock, times(1)).sortProductsByNameAsc(anyList());
    }

    @Test
    @DisplayName("Should handle sorting by name descending")
    void testHandleSorting_NameDesc() {
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of());
        Scanner scanner = new Scanner("4\n");
        catalogMenu.handleOptions("1", scanner);
        verify(sortProductsServiceMock, times(1)).sortProductsByNameDesc(anyList());
    }

    @Test
    @DisplayName("Should handle invalid sorting option")
    void testHandleSorting_InvalidOption() {
        Scanner scanner = new Scanner("99\n");
        catalogMenu.handleOptions("1", scanner);
        assertTrue(outContent.toString().contains("Invalid sorting option."));
        assertTrue(outContent.toString().contains("No products found."));
    }

    @Test
    @DisplayName("Should filter products by starting letter successfully")
    void testHandleFiltering_Letter_Success() {
        Product p = new Product(1L, "Apple", BigDecimal.ONE);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(p));
        when(filterProductsServiceMock.filterProductsByLetterBeginWith(eq('A'), anyList())).thenReturn(List.of(p));

        Scanner scanner = new Scanner("1\nA\n");
        catalogMenu.handleOptions("2", scanner);

        verify(filterProductsServiceMock, times(1)).filterProductsByLetterBeginWith(eq('A'), anyList());
        assertTrue(outContent.toString().contains("Apple"));
    }

    @Test
    @DisplayName("Should handle empty input when filtering by letter")
    void testHandleFiltering_Letter_EmptyInput() {
        Scanner scanner = new Scanner("1\n\n");
        catalogMenu.handleOptions("2", scanner);
        assertTrue(outContent.toString().contains("Input cannot be empty."));
    }

    @Test
    @DisplayName("Should filter products by price higher than successfully")
    void testHandleFiltering_PriceHigher_Success() {
        Product p = new Product(1L, "Phone", BigDecimal.valueOf(500));
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(p));
        when(filterProductsServiceMock.filterProductsByPriceHigherThan(eq(new BigDecimal("100")), anyList())).thenReturn(List.of(p));

        Scanner scanner = new Scanner("2\n100\n");
        catalogMenu.handleOptions("2", scanner);

        verify(filterProductsServiceMock, times(1)).filterProductsByPriceHigherThan(eq(new BigDecimal("100")), anyList());
        assertTrue(outContent.toString().contains("Phone"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when entering invalid price format for higher-than filter")
    void testHandleFiltering_PriceHigher_InvalidFormat() {
        Scanner scanner = new Scanner("2\nabc\n");
        catalogMenu.handleOptions("2", scanner);
        assertTrue(outContent.toString().contains("Invalid price format! Please enter a valid number."));
    }

    @Test
    @DisplayName("Should filter products by price lower than successfully")
    void testHandleFiltering_PriceLower_Success() {
        Product p = new Product(1L, "Candy", BigDecimal.ONE);
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(p));
        when(filterProductsServiceMock.filterProductsByPriceLowerThan(eq(new BigDecimal("50")), anyList())).thenReturn(List.of(p));

        Scanner scanner = new Scanner("3\n50\n");
        catalogMenu.handleOptions("2", scanner);

        verify(filterProductsServiceMock, times(1)).filterProductsByPriceLowerThan(eq(new BigDecimal("50")), anyList());
        assertTrue(outContent.toString().contains("Candy"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException for lower-than filter")
    void testHandleFiltering_PriceLower_InvalidFormat() {
        Scanner scanner = new Scanner("3\nxyz\n");
        catalogMenu.handleOptions("2", scanner);
        assertTrue(outContent.toString().contains("Invalid price format! Please enter a valid number."));
    }

    @Test
    @DisplayName("Should handle invalid filter option")
    void testHandleFiltering_InvalidOption() {
        Scanner scanner = new Scanner("99\n");
        catalogMenu.handleOptions("2", scanner);
        assertTrue(outContent.toString().contains("Invalid filter option."));
        assertTrue(outContent.toString().contains("No products match your filter."));
    }

    @Test
    @DisplayName("Should handle searching products by keyword successfully")
    void testHandleSearching_Success() {
        Product p = new Product(1L, "Smart TV", BigDecimal.valueOf(800));
        when(marketPlaceServiceMock.getAllProducts()).thenReturn(List.of(p));
        when(filterProductsServiceMock.filterProductsByName(eq("TV"), anyList())).thenReturn(List.of(p));

        Scanner scanner = new Scanner("TV\n");
        catalogMenu.handleOptions("3", scanner);

        verify(filterProductsServiceMock, times(1)).filterProductsByName(eq("TV"), anyList());
        assertTrue(outContent.toString().contains("Smart TV"));
    }

    @Test
    @DisplayName("Should handle searching when no products match keyword")
    void testHandleSearching_NoMatch() {
        when(filterProductsServiceMock.filterProductsByName(anyString(), anyList())).thenReturn(Collections.emptyList());

        Scanner scanner = new Scanner("NonExistent\n");
        catalogMenu.handleOptions("3", scanner);

        assertTrue(outContent.toString().contains("No products match your keyword."));
    }
}