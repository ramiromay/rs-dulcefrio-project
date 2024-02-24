package com.realssoft.dulcefrio.api.model.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SaleDTO (
        UUID id,
        TicketDTO ticket,
        ProductDTO product,
        Integer numberProduct
){
}
