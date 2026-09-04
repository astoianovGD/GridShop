package com.bobocode.services.products;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.products.ProductCreateMapper;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.mappers.users.UserMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
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

import java.util.List;
import java.util.function.Consumer;

/**
 * Service for managing products within the marketplace.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MarketPlaceService {

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
     * Repository for managing bucket items.
     */
    private final BucketItemRepository bucketItemRepository;

    /**
     * Mapper for users.
     */
    private final UserMapper userMapper;

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
    public List<UserDto> removeProduct(final Long productId) {
        Product product = productRepository
                .findProductByIsActiveAndId(true, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id " + productId + " not found!"
                ));

        List<User> usersWithProduct = bucketItemRepository
                .findActiveUsersByActiveProductIdInBucket(productId);

        bucketItemRepository.deleteAllByProductId(productId);

        product.setActive(false);
        productRepository.save(product);

        return usersWithProduct.stream()
                .map(userMapper::toDto)
                .toList();
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

        productRepository.save(existingProduct);
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
     * Retrieves a product from the marketplace by its ID.
     *
     * @param productId the ID of the product to retrieve
     * @return the requested product
     * @throws EntityNotFoundException if the product is not found
     */
    public ProductDto getProductById(final Long productId) {
        Product product = productRepository
                .findProductByIsActiveAndId(true, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id " + productId + " not found!"
                ));

        return productMapper.toDto(product);
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

        productRepository.save(existingProduct);
    }
}
