package com.bobocode.controllers.products;

import com.bobocode.dto.products.ProductCreateDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.products.ProductFilterDto;
import com.bobocode.services.products.MarketPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final MarketPlaceService marketPlaceService;

    /**
     * Retrieves products with optional filtering, sorting, or returns all if no params provided.
     * GET /api/v1/products
     */
    @GetMapping
    public Page<ProductDto> getProducts(
            ProductFilterDto filter,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return marketPlaceService.getProducts(filter, pageable);
    }

    /**
     * Retrieves a specific product by its ID.
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return marketPlaceService.getProductById(id);
    }

    /**
     * Creates a new product.
     * POST /api/v1/products
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(@RequestBody @Validated ProductCreateDto productCreateDto) {
        marketPlaceService.addNewProduct(productCreateDto);
    }

    /**
     * Updates an existing product by its ID.
     * PUT /api/v1/products/{id}
     */
    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id, @RequestBody @Validated ProductDto productDto) {
        marketPlaceService.editProduct(id, productDto);
    }

    /**
     * Deletes a product by its ID.
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id) {
        marketPlaceService.removeProduct(id);
    }
}