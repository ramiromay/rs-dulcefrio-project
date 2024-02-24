package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.SaleDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Sale;

import java.awt.Window;
import java.util.UUID;

public class SaleMapper implements Mapper<Sale, SaleDTO>
{

    private static final SaleMapper INSTANCE = new SaleMapper();

    private SaleMapper() {}

    public static SaleMapper getInstance() {
        return INSTANCE;
    }


    @Override
    public Sale toEntity(SaleDTO dto) {
        return Sale.builder()
                .productQuantity(dto.numberProduct())
                .build();
    }

    @Override
    public SaleDTO toDto(Sale entity) {
        return SaleDTO.builder()
                .ticket(TicketDTO.builder()
                        .id(entity.getTicket().getId())
                        .employee(EmployeeDTO.builder()
                                .name(entity.getTicket().getEmployee().getName())
                                .lastName(entity.getTicket().getEmployee().getLastName())
                                .build())
                        .timestamp(entity.getTicket().getTimestamp())
                        .amount(entity.getTicket().getPay())
                        .totalPrice(entity.getTicket().getTotalPrice())
                        .build())
                .product(ProductDTO.builder()
                        .name(entity.getProduct().getName())
                        .price(entity.getProduct().getPrice())
                        .build())
                .numberProduct(entity.getProductQuantity())
                .build();
    }
}
