package com.bobocode.Entities.Products;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void testCategoryGettersAndSetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Electronics");
    }

    @Test
    void testCategoryEqualsAndHashCode() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Electronics");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Electronics");

        assertThat(category1).isEqualTo(category2);
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }

    @Test
    void testCategoryToString() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        assertThat(category.toString()).contains("Electronics", "1");
    }
}