package com.realssoft.dulcefrio.api.persistence.dao;

import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.persistence.entity.User;

import java.util.UUID;

public interface EmployeeDao extends Dao <EmployeeDTO, UUID>
{

    boolean isEmployeeExist(UUID id, String name, String lastName);
    boolean isEmailExist(UUID id,String email);
    User getUserByEmployeeId(UUID id);

}
