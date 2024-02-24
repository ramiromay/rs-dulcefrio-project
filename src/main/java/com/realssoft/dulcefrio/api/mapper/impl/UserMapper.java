package com.realssoft.dulcefrio.api.mapper.impl;

import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.User;

public class UserMapper implements Mapper<User, UserDTO>
{

    private static final UserMapper INSTANCE = new UserMapper();

    private UserMapper() {}

    public static UserMapper getInstance()
    {
        return INSTANCE;
    }

    @Override
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.id())
                .username(dto.username())
                .password(dto.password())
                .creationDate(dto.creationDate())
                .modificationDate(dto.modificationDate())
                .available(dto.available())
                .active(dto.active())
                .build();
    }

    @Override
    public UserDTO toDto(User entity)
    {
        Employee employee = entity.getEmployee();
        return UserDTO.builder()
                .username(entity.getUsername())
                .creationDate(entity.getCreationDate())
                .modificationDate(entity.getModificationDate())
                .available(entity.getAvailable())
                .active(entity.getActive())
                .employee(
                        EmployeeDTO.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .lastName(employee.getLastName())
                        .build()
                )
                .role(entity.getRole().getName())
                .build();
    }
}
