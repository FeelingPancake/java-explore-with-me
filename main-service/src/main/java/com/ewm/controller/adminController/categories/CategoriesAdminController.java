package com.ewm.controller.adminController.categories;

import com.ewm.service.category.CategoryService;
import dtostorage.main.category.CategoryDto;
import dtostorage.main.category.NewCategoryDto;
import dtostorage.main.category.UpdateCategoryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoriesAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable(name = "catId") Long catId,
                                      @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        return categoryService.updateCategory(catId, updateCategoryDto);
    }
}
