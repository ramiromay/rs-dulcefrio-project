package com.realssoft.dulcefrio.api.model.dto;

import java.util.UUID;

public record AccountResponse(
        UUID userId,
        String token,
        boolean isAdmin
) {
}
