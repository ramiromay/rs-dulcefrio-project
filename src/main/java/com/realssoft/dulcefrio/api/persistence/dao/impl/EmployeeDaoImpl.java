package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.exception.NoChangesException;
import com.realssoft.dulcefrio.api.exception.ResourcesNotFoundException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.EmployeeMapper;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

import java.awt.Dialog;
import java.awt.Window;
import java.util.Collections;
import jakarta.transaction.Transactional;
import org.hibernate.HibernateException;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.UUID;

public class EmployeeDaoImpl implements EmployeeDao
{

    private static final EmployeeDaoImpl INSTANCE = new EmployeeDaoImpl();
    private static Window pageManager;
    private final Mapper<Employee, EmployeeDTO> mapper = EmployeeMapper.getInstance();
    private EntityManager entityManager;

    private EmployeeDaoImpl() {}

    public static EmployeeDaoImpl getInstance(Window d)
    {
        pageManager = d;
        return INSTANCE;
    }

    private void close()
    {
        if (entityManager != null)
        {
            entityManager.close();
        }
    }

    @Transactional
    @Override
    public UUID save(EmployeeDTO entity)
    {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }
            Employee newEmployee = mapper.toEntity(entity);
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

    @Transactional
    @Override
    public UUID update(UUID id, EmployeeDTO entity)
    {
        try
        {
            if (id == null || entity == null)
            {
                throw new InvalidArgumentException(
                        "Error de Modificar Empleado",
                        "No se pudo realizar la modificación del empleado debido a datos no válidos."
                );
            }
            entityManager = HibernateUtils.getEntityManager();
            Employee foundEmployee = entityManager.find(Employee.class, id);
            entityManager.getTransaction().begin();
            if (foundEmployee == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Modificar Empleado",
                        "No se puede realizar la modificación. El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            foundEmployee.setName(entity.name());
            foundEmployee.setLastName(entity.lastName());
            foundEmployee.setEmail(entity.email());
            foundEmployee.setPhone(entity.phone());
            foundEmployee.setAdmissionDate(entity.admissionDate());
            entityManager.getTransaction().commit();
            return foundEmployee.getId();
        }
        catch (NoChangesException | InvalidArgumentException | ResourcesNotFoundException | TransactionRequiredException |
               OptimisticLockException | HibernateException e)
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
            else if (e instanceof ResourcesNotFoundException ex)
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        ex.getMessage(),
                        ex.getTitle(),
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else if  (e instanceof NoChangesException ex)
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        ex.getMessage(),
                        ex.getTitle(),
                        JOptionPane.WARNING_MESSAGE
                );
            }
            else
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        "No se pudo realizar la modificación del empleado.",
                        "Error al modificar empleado",
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

    @Transactional
    @Override
    public void delete(UUID id)
    {
        try
        {
            if (id == null)
            {
                throw new InvalidArgumentException(
                        "Error al Eliminar Empleado",
                        "No se pudo realizar la eliminación del empleado debido a datos no válidos."
                );
            }
            entityManager = HibernateUtils.getEntityManager();
            entityManager.getTransaction().begin();
            Employee employee = entityManager.getReference(Employee.class, id);
            entityManager.remove(employee);
            entityManager.getTransaction().commit();
        }
        catch (InvalidArgumentException |EntityNotFoundException |
               IllegalStateException | TransactionRequiredException e)
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
                        "No se pudo realizar la eliminación del empleado.",
                        "Error al eliminar empleado",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            e.printStackTrace();
        }
        finally
        {
            close();
        }
    }

    @Override
    public EmployeeDTO findById(UUID id)
    {
        try
        {
            if (id == null) {
                throw new InvalidArgumentException(
                        "Error al Buscar Empleado",
                        "No se pudo realizar la búsqueda del empleado debido a datos no válidos."
                );
            }
            entityManager = HibernateUtils.getEntityManager();
            Employee foundEmployee = entityManager.find(Employee.class, id);
            if (foundEmployee == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Buscar Empleado",
                        "El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            return mapper.toDto(foundEmployee);
        }
        catch (InvalidArgumentException | ResourcesNotFoundException |EntityNotFoundException | HibernateException e)
        {
            if (e instanceof ResourcesNotFoundException ex)
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        ex.getMessage(),
                        ex.getTitle(),
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else if (e instanceof InvalidArgumentException ex)
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
                        "No se pudo realizar la busqueda del empleado.",
                        "Error al buscar empleado",
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
    public List<EmployeeDTO> findAll()
    {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            CriteriaQuery<Object> cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Employee.class));
            Query q = entityManager.createQuery(cq);
            List<Employee> employees =  q.getResultList();
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

    @Override
    public boolean isEmployeeExist(UUID id,String name, String lastName) {
       try
       {
           entityManager = HibernateUtils.getEntityManager();
           if (id == null)
           {
               TypedQuery<Long> query = entityManager.createQuery(
                       "SELECT COUNT(e) FROM Employee e " +
                               "WHERE e.name = :name AND e.lastName = :lastName",
                       Long.class
               );
               query.setParameter("name", name);
               query.setParameter("lastName", lastName);
               return query.getSingleResult() > 0;
           }

           TypedQuery<Long> query = entityManager.createQuery(
                   "SELECT COUNT(e) FROM Employee e " +
                           "WHERE e.name = :name AND e.lastName = :lastName AND e.id <> :id",
                   Long.class
           );
           query.setParameter("name", name);
           query.setParameter("lastName", lastName);
           query.setParameter("id", id);
           return query.getSingleResult() > 0;
       }
       catch (IllegalStateException | PersistenceException e)
       {
           JOptionPane.showMessageDialog(
                   pageManager,
                   "No se pudo realizar la busqueda del empleado.",
                   "Error al Buscar Empleado",
                   JOptionPane.ERROR_MESSAGE
           );
           e.printStackTrace();
       }
       finally
       {
           close();
       }
       return false;
    }

    @Override
    public boolean isEmailExist(UUID id, String email) {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            if (id == null)
            {
                TypedQuery<Long> query = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Employee e " +
                                "WHERE e.email = :email",
                        Long.class
                );
                query.setParameter("email", email);
                return query.getSingleResult() > 0;
            }

            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(e) FROM Employee e " +
                            "WHERE e.email = :email AND e.id <> :id",
                    Long.class
            );
            query.setParameter("email", email);
            query.setParameter("id", id);
            return query.getSingleResult() > 0;

        }
        catch (IllegalStateException | PersistenceException e)
        {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda del email.",
                    "Error al Buscar Empleado",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
        finally
        {
            close();
        }
        return false;
    }

    @Override
    public User getUserByEmployeeId(UUID id)
    {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u " +
                            "WHERE u.employee.id = :id",
                    User.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        }
        catch (IllegalStateException | PersistenceException e)
        {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda del usuario.",
                    "Error al Buscar Usuario",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
        finally
        {
            close();
        }
        return null;
    }
}
