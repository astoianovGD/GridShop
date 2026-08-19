package com.bobocode.controllers.products;

import com.bobocode.dto.products.ProductCreateDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.filtering.FilterProductService;
import com.bobocode.services.products.sorting.SortProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final MarketPlaceService marketPlaceService;
    private final FilterProductService filterProductService;
    private final SortProductService sortProductService;

    /**
     * Retrieves products with optional filtering, sorting, or returns all if no params provided.
     * GET /api/v1/products
     */
    @GetMapping
    public List<ProductDto> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startsWith,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        if (startsWith != null) {
            return filterProductService.filterByNameStartingWith(startsWith);
        }
        if (search != null) {
            return filterProductService.filterByNameContains(search);
        }
        if (minPrice != null) {
            return filterProductService.filterByPriceGreaterThan(minPrice);
        }
        if (maxPrice != null) {
            return filterProductService.filterByPriceLowerThan(maxPrice);
        }

        if (sortBy != null) {
            boolean isAsc = "asc".equalsIgnoreCase(direction);
            if ("price".equalsIgnoreCase(sortBy)) {
                return isAsc ? sortProductService.filterByPriceAsc() : sortProductService.filterByProductDesc();
            } else if ("name".equalsIgnoreCase(sortBy)) {
                return isAsc ? sortProductService.filterByNameAsc() : sortProductService.filterByNameDesc();
            }
        }


        return marketPlaceService.getAllProducts();
    }

    /**
     * Retrieves a specific product by its ID.
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable long id) {
        return marketPlaceService.getProductById(id);
    }

    /**
     * Creates a new product.
     * POST /api/v1/products
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(@RequestBody ProductCreateDto productCreateDto) {
        marketPlaceService.addNewProduct(productCreateDto);
    }

    /**
     * Updates an existing product by its ID.
     * PUT /api/v1/products/{id}
     */
    @PutMapping("/{id}")
    public void updateProduct(@PathVariable long id, @RequestBody ProductDto productDto) {
        marketPlaceService.editProduct(id, productDto);
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable long id) {
        marketPlaceService.removeProduct(id);
    }
}