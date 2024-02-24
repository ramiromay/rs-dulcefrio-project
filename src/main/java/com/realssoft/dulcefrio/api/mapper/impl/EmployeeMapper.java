package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;

public class EmployeeMapper implements Mapper<Employee, EmployeeDTO>
{

    private static final EmployeeMapper INSTANCE = new EmployeeMapper();

    private EmployeeMapper() {}

    public static EmployeeMapper getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Employee toEntity(EmployeeDTO dto)
    {
        if (dto == null) return null;
        return Employee.builder()
                .id(dto.id())
                .name(dto.name())
                .lastName(dto.lastName())
                .phone(dto.phone())
                .email(dto.email())
                .admissionDate(dto.admissionDate())
                .build();
    }

    @Override
    public EmployeeDTO toDto(Employee entity)
    {
        if (entity == null) return null;
        return EmployeeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .admissionDate(entity.getAdmissionDate())
                .build();
    }

}
