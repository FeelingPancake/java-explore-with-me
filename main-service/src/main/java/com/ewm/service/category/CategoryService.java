package com.ewm.service.category;

import dtostorage.main.category.CategoryDto;
import dtostorage.main.category.NewCategoryDto;
import dtostorage.main.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto getCategory(Long categoryId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, UpdateCategoryDto updateCategoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);
}

