package com.ewm.util.mapper.category;

import com.ewm.model.Category;
import dtostorage.main.category.CategoryDto;
import dtostorage.main.category.NewCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto categoryDto);
}
