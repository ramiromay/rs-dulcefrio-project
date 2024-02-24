package com.realssoft.dulcefrio;

import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.dao.impl.EmployeeDaoImpl;
import com.realssoft.dulcefrio.api.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestEmployeeDao
{

    private static EmployeeDao employeeDao;

    @BeforeAll
    static void setUp()
    {
        employeeDao = EmployeeDaoImpl.getInstance(null);
    }

    @Order(1)
    @Test
    void testSave()
    {
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .name("Jesus Joaquin")
                .lastName("Esparza Quijano")
                .phone(Long.parseLong("9993327010"))
                .email("joaquintalcual123@gmail.com")
                .admissionDate(DateUtils.convertedDateMySqlFormat("2023-10-15"))
                .build();
        UUID idEmployee = employeeDao.save(employeeDTO);
        Assertions.assertNotNull(idEmployee);
    }

    @Order(2)
    @Test
    void testUpdate()
    {
        UUID idEmployee = UUID.fromString("c15213fa-d918-436b-8cb5-7637915c1260");
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .name("Eduadoooooo")
                .lastName("May Moo")
                .phone(Long.parseLong("9993327010"))
                .email("ramiromay12@gmail.com")
                .admissionDate(DateUtils.convertedDateMySqlFormat("2023-11-15"))
                .build();
        idEmployee = employeeDao.update(idEmployee, employeeDTO);
        Assertions.assertNotNull(idEmployee);
    }

    @Order(3)
    @Test
    void testGet()
    {
        UUID idEmployee = UUID.fromString("87d2e6ad-8a4f-42ee-b65c-2b154a2ecae3");
        EmployeeDTO employeeDTO = employeeDao.findById(idEmployee);
        Assertions.assertNotNull(employeeDTO);
        Assertions.assertEquals("Ramiro", employeeDTO.name());
        Assertions.assertEquals("May Moo", employeeDTO.lastName());
        Assertions.assertEquals("ramiromay1@gmail.com", employeeDTO.email());
        Assertions.assertEquals("2022-05-01", employeeDTO.admissionDate().toString());
        Assertions.assertEquals(Long.parseLong("9994978961"), employeeDTO.phone());
    }

    @Order(4)
    @Test
    void testDelete()
    {
        UUID idEmployee = UUID.fromString("a7662642-f4a9-4f07-b64c-910e229e3535");
        employeeDao.delete(idEmployee);
        Assertions.assertNull(employeeDao.findById(idEmployee));
    }

    @Order(5)
    @Test
    void testGetAll()
    {
        Assertions.assertFalse(employeeDao.findAll().isEmpty());
    }

}
