package com.realssoft.dulcefrio.api.persistence.dao;

import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import java.util.UUID;

public interface ShoppingCartDao extends Dao<ShoppingCartDTO, UUID>
{

    boolean isEnoughProduct(String name, Integer numberProduct);

}
