package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Product;
import com.realssoft.dulcefrio.api.persistence.entity.ShoppingCart;

public class ShoppingCartMapper implements Mapper<ShoppingCart, ShoppingCartDTO>
{

    private static final ShoppingCartMapper INSTANCE = new ShoppingCartMapper();

    private ShoppingCartMapper() {}

    public static ShoppingCartMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ShoppingCart toEntity(ShoppingCartDTO dto) {
        if (dto == null)return null;
        return ShoppingCart.builder()
                .numberProduct(dto.quantity())
                .build();
    }

    @Override
    public ShoppingCartDTO toDto(ShoppingCart entity) {
        if (entity == null) return null;
        Product product = entity.getProduct();
        return ShoppingCartDTO.builder()
                .id(entity.getId())
                .product(ProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .quantity(entity.getNumberProduct())
                .build();
    }
}
