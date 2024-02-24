package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;

@Builder
public record CategoryTypeDTO(
        String name,
        String description
) {
}
