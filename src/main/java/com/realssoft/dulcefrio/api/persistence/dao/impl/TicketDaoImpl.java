package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.EmployeeMapper;
import com.realssoft.dulcefrio.api.mapper.impl.TicketMapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.persistence.dao.TicketDao;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Ticket;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;

import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TicketDaoImpl implements TicketDao
{

    private static final TicketDaoImpl INSTANCE = new TicketDaoImpl();
    private static Window pageManager;
    private final Mapper<Ticket,TicketDTO> mapper = TicketMapper.getInstance();
    private EntityManager entityManager;

    private TicketDaoImpl() {}

    public static TicketDaoImpl getInstance(Window w) {
        pageManager = w;
        return INSTANCE;
    }

    private void close()
    {
        if (entityManager != null)
        {
            entityManager.close();
        }
    }

    private UUID getEmployeeId()
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            User user = entityManager1.find(User.class, Main.ID_USER);
            return user.getEmployee().getId();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        return null;
    }

    private Employee getEmployee(UUID id)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            return entityManager1.find(Employee.class, id);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID save(TicketDTO entity) {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }
            Employee employee = getEmployee(getEmployeeId());
            Ticket newEmployee = mapper.toEntity(entity);
            newEmployee.setEmployee(employee);
            entityManager = HibernateUtils.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(newEmployee);
            entityManager.getTransaction().commit();
            return newEmployee.getId();
        }
        catch (InvalidArgumentException | IllegalArgumentException | PersistenceException e)
        {
            if (e instanceof InvalidArgumentException ex)
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        ex.getMessage(),
                        ex.getTitle(),
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        "No se pudo registrar al empleado intentelo mas tarde",
                        "Error al Registrar Empleado",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            e.printStackTrace();
        }
        finally
        {
            close();
        }
        return null;
    }

    @Override
    public UUID update(UUID id, TicketDTO entity) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public TicketDTO findById(UUID id) {
        return null;
    }

    @Override
    public List<TicketDTO> findAll() {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            CriteriaQuery<Object> cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ticket.class));
            Query q = entityManager.createQuery(cq);
            List<Ticket> employees =  q.getResultList();
            return employees.stream()
                    .map(mapper::toDto)
                    .toList();
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda del empleado.",
                    "Error al buscar empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        finally
        {
            close();
        }

        return Collections.emptyList();
    }
}
