package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;

@Builder
public record CategoryDTO (
        String name,
        CategoryTypeDTO type
)
{
}
