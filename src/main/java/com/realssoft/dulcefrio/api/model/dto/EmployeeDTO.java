package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;
import java.util.Date;
import java.util.UUID;

@Builder
public record EmployeeDTO(
        UUID id,
        String name,
        String lastName,
        Long phone,
        String email,
        Date admissionDate)
{}
