package com.realssoft.dulcefrio.api.persistence.dao;

import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Role;

import java.util.UUID;

public interface UserDao extends Dao<UserDTO, UUID>
{

    Employee findEmployeeByFullName(EmployeeDTO employee);
    Role findRoleByName(String role);
    boolean isUsernameExist(UUID id, String username);

}
