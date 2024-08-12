package com.ewm.util.mapper.category;

import com.ewm.model.Category;
import dtostorage.main.category.CategoryDto;
import dtostorage.main.category.NewCategoryDto;
import dtostorage.main.category.UpdateCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Category toCategory(NewCategoryDto categoryDto);

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "name", expression = "java(updateCategoryDto.getName() == null ? category.getName() : updateCategoryDto.getName())")
    Category toCategory(UpdateCategoryDto updateCategoryDto, Category category);
}
