package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Ticket;

import java.util.Date;

public class TicketMapper implements Mapper<Ticket, TicketDTO>
{

    private static final TicketMapper INSTANCE = new TicketMapper();

    private TicketMapper(){}

    public static TicketMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Ticket toEntity(TicketDTO dto) {
        return Ticket.builder()
                .pay(dto.amount())
                .totalPrice(dto.totalPrice())
                .timestamp(new Date())
                .build();
    }

    @Override
    public TicketDTO toDto(Ticket entity) {
        return TicketDTO.builder()
                .id(entity.getId())
                .employee(EmployeeDTO.builder()
                        .name(entity.getEmployee().getName())
                        .lastName(entity.getEmployee().getLastName())
                        .id(entity.getEmployee().getId())
                        .build())
                .timestamp(entity.getTimestamp())
                .amount(entity.getPay())
                .totalPrice(entity.getTotalPrice())
                .build();
    }
}
