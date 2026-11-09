package com.bobocode.services.products;

import com.bobocode.configs.RedisCacheConfig;
import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.dto.products.CategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.admin.auto-create=false",
        "spring.cache.type=simple"
})
class RedisCachingServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("getAllCategories should populate cache and addNewCategory should evict it")
    void shouldCacheCategoriesAndEvictOnWrite() {
        Cache categoriesCache = cacheManager.getCache(RedisCacheConfig.CATEGORIES_CACHE);
        assertNotNull(categoriesCache);
        categoriesCache.clear();

        // First call populates the cache
        List<CategoryDto> categories = categoryService.getAllCategories();
        assertFalse(categories.isEmpty());
        assertNotNull(categoriesCache.get(org.springframework.cache.interceptor.SimpleKey.EMPTY));

        // Adding a new category evicts the cache
        CategoryCreateDto newCategory = new CategoryCreateDto();
        newCategory.setName("CachedBooksCategory");
        categoryService.addNewCategory(newCategory);

        assertNull(categoriesCache.get(org.springframework.cache.interceptor.SimpleKey.EMPTY));
    }
}
