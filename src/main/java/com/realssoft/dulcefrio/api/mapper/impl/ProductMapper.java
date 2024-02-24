package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.CategoryTypeDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Category;
import com.realssoft.dulcefrio.api.persistence.entity.CategoryType;
import com.realssoft.dulcefrio.api.persistence.entity.Product;

public class ProductMapper implements Mapper<Product, ProductDTO>
{

    private static final ProductMapper INSTANCE = new ProductMapper();

    private ProductMapper() {}

    public static ProductMapper getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Product toEntity(ProductDTO dto)
    {
        if (dto == null) return null;
        return Product.builder()
                .name(dto.name())
                .price(dto.price())
                .stock(dto.stock())
                .available(dto.isAvailable())
                .build();
    }

    @Override
    public ProductDTO toDto(Product entity)
    {
        if (entity == null) return null;
        Category  category = entity.getCategory();
        CategoryType categoryType = category.getCategoryType();
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .category(CategoryDTO.builder()
                        .name(category.getName())
                        .type(CategoryTypeDTO.builder()
                                .name(categoryType.getName())
                                .description(categoryType.getDescription())
                                .build()
                        )
                        .build()
                )
                .isAvailable(entity.getAvailable())
                .build();
    }
}
