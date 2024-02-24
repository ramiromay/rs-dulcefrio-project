package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record ShoppingCartDTO(
        UUID id,
        EmployeeDTO employee,
        ProductDTO product,
        Integer quantity
) {
}
