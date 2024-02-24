package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.CategoryTypeDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Category;
import com.realssoft.dulcefrio.api.persistence.entity.CategoryType;

public class CategoryMapper implements Mapper<Category, CategoryDTO>
{

    private static final CategoryMapper INSTANCE = new CategoryMapper();

    private CategoryMapper() {}


    public static CategoryMapper getInstance()
    {
        return INSTANCE;
    }


    @Override
    public Category toEntity(CategoryDTO dto) {
        return null;
    }

    @Override
    public CategoryDTO toDto(Category entity) {
        if (entity == null) return null;
        CategoryType categoryType = entity.getCategoryType();
        return CategoryDTO.builder()
                .name(entity.getName())
                .type(
                        CategoryTypeDTO.builder()
                                .name(categoryType.getName())
                                .description(categoryType.getDescription())
                                .build()
                )
                .build();
    }
}
