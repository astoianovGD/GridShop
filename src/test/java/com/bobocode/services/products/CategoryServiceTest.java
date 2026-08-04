package com.bobocode.services.products;

import com.bobocode.utility.CustomJdbcTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CustomJdbcTemplate customJdbcTemplate;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addNewCategory_WhenNotExists_ShouldExecuteInsert() {
        when(customJdbcTemplate.findOne(anyString(), any(Function.class), eq("Electronics"))).thenReturn(false);

        categoryService.addNewCategory("Electronics");

        verify(customJdbcTemplate, times(1)).execute(anyString(), (Object[]) any());
    }

    @Test
    void addNewCategory_WhenExists_ShouldThrowException() {
        when(customJdbcTemplate.findOne(anyString(), any(Function.class), eq("Electronics"))).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.addNewCategory("Electronics")
        );
        assertTrue(exception.getMessage().contains("already exists"));
        verify(customJdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }

    @Test
    void removeCategory_WhenHasNoProducts_ShouldExecuteDelete() {
        when(customJdbcTemplate.findOne(anyString(), any(Function.class), eq(1L))).thenReturn(false);

        categoryService.removeCategory(1L);

        verify(customJdbcTemplate, times(1)).execute(anyString(), (Object[]) any());
    }

    @Test
    void removeCategory_WhenHasProducts_ShouldThrowException() {
        when(customJdbcTemplate.findOne(anyString(), any(Function.class), eq(1L))).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> categoryService.removeCategory(1L)
        );
        assertTrue(exception.getMessage().contains("still contains products"));
        verify(customJdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }
}