package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String username,
        String password,
        EmployeeDTO employee,
        Date creationDate,
        Date modificationDate,
        String role,
        Boolean available,
        Boolean active,
        String token
) {
}
