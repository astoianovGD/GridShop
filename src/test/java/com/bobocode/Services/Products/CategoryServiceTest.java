package com.bobocode.Services.Products;

import com.bobocode.Utility.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addNewCategory_WhenNotExists_ShouldExecuteInsert() {
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq("Electronics"))).thenReturn(false);

        categoryService.addNewCategory("Electronics");

        verify(jdbcTemplate, times(1)).execute(anyString(), (Object[]) any());
    }

    @Test
    void addNewCategory_WhenExists_ShouldThrowException() {
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq("Electronics"))).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.addNewCategory("Electronics")
        );
        assertTrue(exception.getMessage().contains("already exists"));
        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }

    @Test
    void removeCategory_WhenHasNoProducts_ShouldExecuteDelete() {
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(1L))).thenReturn(false);

        categoryService.removeCategory(1L);

        verify(jdbcTemplate, times(1)).execute(anyString(), (Object[]) any());
    }

    @Test
    void removeCategory_WhenHasProducts_ShouldThrowException() {
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(1L))).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> categoryService.removeCategory(1L)
        );
        assertTrue(exception.getMessage().contains("still contains products"));
        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }
}