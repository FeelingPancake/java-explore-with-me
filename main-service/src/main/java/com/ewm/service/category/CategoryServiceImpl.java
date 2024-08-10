package com.ewm.service.category;

import com.ewm.exception.ConfilctException;
import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Category;
import com.ewm.repository.CategoryRepository;
import com.ewm.util.mapper.category.CategoryMapper;
import dtostorage.main.category.CategoryDto;
import dtostorage.main.category.NewCategoryDto;
import dtostorage.main.category.UpdateCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotExistsExeption("Категории с id - " + categoryId + " нет."));

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category createdCategory = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));

        return categoryMapper.toCategoryDto(createdCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
            () -> new NotExistsExeption("Категории с id - " + categoryId + " нет."));

        if (!category.getEvents().isEmpty()) {
            throw new ConfilctException("Категорию нельзя удалить, т.к. с ней связаны события");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryDto updateCategoryDto) {
        Category savedCategory = categoryRepository.findById(categoryId).orElseThrow(
            () -> new NotExistsExeption("Категории с id - " + categoryId + " нет."));

        Category category = categoryMapper.toCategory(updateCategoryDto, savedCategory);

        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return categoryRepository.findAll(pageable).toList().stream().map(categoryMapper::toCategoryDto).collect(
            Collectors.toList());
    }
}
