package com.realssoft.dulcefrio.api.persistence.dao;

import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Category;

import java.util.List;
import java.util.UUID;

public interface ProductDao extends Dao<ProductDTO, UUID>
{

    List<CategoryDTO> findAllCategory();
    Category  findCategoryByName(String name);
    boolean isProductExist(UUID id, String name);

}
