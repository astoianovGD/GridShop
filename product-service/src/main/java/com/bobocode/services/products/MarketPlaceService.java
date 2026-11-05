package com.bobocode.services.products;

import com.bobocode.concurrency.cache.LruCache;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.products.ProductCreateMapper;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bobocode.dto.products.ProductFilterDto;
import com.bobocode.enums.SearchOperations;
import com.bobocode.services.filter_and_search.GenericSpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service for managing products within the marketplace.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MarketPlaceService {

    private static final int DEFAULT_CACHE_CAPACITY = 100_000;

    /**
     * Thread-safe local LRU cache for products by ID.
     */
    private final LruCache<Long, ProductDto> productCache = new LruCache<>(DEFAULT_CACHE_CAPACITY);

    /**
     * Repository for managing products.
     */
    private final ProductRepository productRepository;

    /**
     * Mapper for products.
     */
    private final ProductMapper productMapper;

    /**
     * Mapper for product creation.
     */
    private final ProductCreateMapper productCreateMapper;

    /**
     * Repository for managing categories.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Adds a new product to the marketplace.
     *
     * @param createDto the product to be added
     */
    @Transactional
    public void addNewProduct(
            final com.bobocode.dto.products.ProductCreateDto createDto
    ) {
        Product product = productCreateMapper.toEntity(createDto);

        Category category = categoryRepository
                .findById(createDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with ID " + createDto.getCategoryId()
                                + " not found!"
                ));

        product.setCategory(category);

        productRepository.save(product);
    }

    /**
     * Removes a product from the marketplace by its ID.
     *
     * @param productId the ID of the product to remove
     * @return list of affected users whose buckets contained the product
     * @throws EntityNotFoundException if the product is not found
     */
    @Transactional
    public void removeProduct(final Long productId) {
        Product product = productRepository
                .findProductByIsActiveAndId(true, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id " + productId + " not found!"
                ));

        product.setActive(false);
        productRepository.save(product);
        productCache.evict(productId);
    }

    /**
     * Edits an existing product in the marketplace.
     *
     * @param productDto the product with updated information
     */
    @Transactional
    public void editProduct(final Long id, final ProductDto productDto) {
        Product existingProduct = productRepository
                .findProductByIsActiveAndId(true, id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id " + id + " not found!"
                ));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());

        Category category = categoryRepository
                .findByName(productDto.getCategoryName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with name '" + productDto.getCategoryName()
                                + "' not found!"
                ));

        existingProduct.setCategory(category);

        Product saved = productRepository.save(existingProduct);
        productCache.put(id, productMapper.toDto(saved));
    }

    /**
     * Retrieves a list of all products in the marketplace.
     *
     * @return a list containing all products
     */
    public List<ProductDto> getAllProducts() {
        return productRepository.findAllByIsActive(true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a paginated list of products matching dynamic filtering and sorting criteria.
     *
     * @param filter   the filter criteria
     * @param pageable pagination and sorting parameters
     * @return a page of product DTOs
     */
    public Page<ProductDto> getProducts(final ProductFilterDto filter, final Pageable pageable) {
        GenericSpecificationsBuilder<Product> builder = new GenericSpecificationsBuilder<>();

        builder.with("isActive", SearchOperations.EQUAL, true);

        if (filter != null) {
            builder.with("name", SearchOperations.LIKE, filter.name())
                   .with("price", SearchOperations.GREATER_THAN, filter.minPrice())
                   .with("price", SearchOperations.LESS_THAN, filter.maxPrice())
                   .with("category.id", SearchOperations.IN, filter.categoryIds());
        }

        Page<Product> productPage = productRepository.findAll(builder.build(), pageable);
        return productPage.map(productMapper::toDto);
    }

    /**
     * Retrieves a product from the marketplace by its ID, using the thread-safe LRU cache.
     *
     * @param productId the ID of the product to retrieve
     * @return the requested product
     * @throws EntityNotFoundException if the product is not found
     */
    public ProductDto getProductById(final Long productId) {
        return productCache.computeIfAbsent(productId, id -> {
            Product product = productRepository
                    .findProductByIsActiveAndId(true, id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product with id " + id + " not found!"
                    ));
            return productMapper.toDto(product);
        });
    }

    /**
     * Populates the local LRU cache with a large number of synthetic ProductDto entries
     * to simulate high Old Generation memory occupancy for JVM Garbage Collection experiments.
     *
     * @param count number of synthetic cache entries to insert
     * @return current cache size after warm-up
     */
    public int warmupCacheForGcTest(final int count) {
        int entries = Math.min(count, productCache.getMaxCapacity());
        for (int i = 1; i <= entries; i++) {
            long syntheticId = 1_000_000L + i;
            ProductDto dto = new ProductDto();
            dto.setId(syntheticId);
            dto.setName("Cached-Product-Item-" + i + "-" + "X".repeat(20));
            dto.setPrice(BigDecimal.valueOf(100.0 + (i % 500)));
            dto.setCategoryName("Cached-Category-" + (i % 50));
            dto.setIsActive(true);
            productCache.put(syntheticId, dto);
        }
        return productCache.size();
    }

    /**
     * Universal method to update specific fields of a product
     * within a transaction.
     *
     * @param productId    the ID of the product to update
     * @param fieldUpdater a lambda representing the field update
     */
    @Transactional
    public void updateProductField(
            final Long productId, final Consumer<Product> fieldUpdater
    ) {
        Product existingProduct = productRepository
                .findProductByIsActiveAndId(true, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id " + productId + " not found!"
                ));

        fieldUpdater.accept(existingProduct);

        Product saved = productRepository.save(existingProduct);
        productCache.put(productId, productMapper.toDto(saved));
    }
}
