package com.realssoft.dulcefrio.api.model.dto;


import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductDTO(
        UUID id,
        String name,
        Double price,
        Integer stock,
        CategoryDTO category,
        Boolean isAvailable
) {
}
