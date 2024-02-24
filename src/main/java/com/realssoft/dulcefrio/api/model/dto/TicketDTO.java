package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@Builder
public record TicketDTO(
        UUID id,
        Date timestamp,
        Double totalPrice,
        Double amount,
        EmployeeDTO employee
) {
}
