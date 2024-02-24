package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.exception.NoChangesException;
import com.realssoft.dulcefrio.api.exception.ResourcesNotFoundException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.UserMapper;
import com.realssoft.dulcefrio.api.model.dto.EmployeeDTO;
import com.realssoft.dulcefrio.api.model.dto.UserDTO;
import com.realssoft.dulcefrio.api.persistence.dao.UserDao;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Role;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;

import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserDaoImpl implements UserDao
{

    private static final UserDaoImpl INSTANCE = new UserDaoImpl();
    private static Window pageManager;
    private final Mapper<User, UserDTO> mapper = UserMapper.getInstance();
    private EntityManager entityManager;

    private UserDaoImpl() {}

    public static UserDaoImpl getInstance(Window  d)
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

    @Override
    public UUID save(UserDTO entity)
    {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Usuario",
                        "No se pudo realizar el registro del usuario debido a datos no válidos."
                );
            }

            entityManager = HibernateUtils.getEntityManager();
            Employee foundEmployee = findEmployeeByFullName(entity.employee());
            Role foundRole = findRoleByName(entity.role());
            User newUser = mapper.toEntity(entity);
            newUser.setPassword(AccountDaoImpl.getInstance().hashPassword(newUser.getPassword()));
            newUser.setEmployee(foundEmployee);
            newUser.setRole(foundRole);
            entityManager.getTransaction().begin();
            entityManager.persist(newUser);
            entityManager.getTransaction().commit();
            return newUser.getId();
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
    public UUID update(UUID id, UserDTO entity) {
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
            User foundUser = entityManager.createQuery(
                            "SELECT u FROM User u " +
                                    "WHERE u.employee.id = :id",
                            User.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
            entityManager.getTransaction().begin();
            if (foundUser == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Modificar Usuario",
                        "No se puede realizar la modificación. El usuario no ha sido encontrado en los recursos disponibles."
                );
            }

            if (entity.password() != null && !entity.password().isEmpty())
            {
                foundUser.setPassword(AccountDaoImpl.getInstance().hashPassword(entity.password()));
            }
            foundUser.setUsername(entity.username());
            foundUser.setRole(findRoleByName(entity.role()));
            foundUser.setAvailable(entity.available());
            foundUser.setModificationDate(new Date());
            entityManager.getTransaction().commit();
            return foundUser.getId();
        }
        catch (NoChangesException | InvalidArgumentException | ResourcesNotFoundException |
               TransactionRequiredException |
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
            else
            {
                JOptionPane.showMessageDialog(
                        pageManager,
                        "No se pudo realizar la modificación del usuario.",
                        "Error al modificar usuario",
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
    public void delete(UUID id)
    {
        try
        {
            if (id == null)
            {
                throw new InvalidArgumentException(
                        "Error al Eliminar usuario",
                        "No se pudo realizar la eliminación del usuario debido a datos no válidos."
                );
            }
            entityManager = HibernateUtils.getEntityManager();
            entityManager.getTransaction().begin();
            User employee = entityManager.getReference(User.class, id);
            entityManager.remove(employee);
            entityManager.getTransaction().commit();
        }
        catch (InvalidArgumentException | EntityNotFoundException |
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
                        "No se pudo realizar la eliminación del usuario.",
                        "Error al eliminar usuario",
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
    public UserDTO findById(UUID id)
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
            User foundUser = entityManager.find(User.class, id);
            if (foundUser == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Buscar Empleado",
                        "El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            return mapper.toDto(foundUser);
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
    public List<UserDTO> findAll()
    {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            CriteriaQuery<Object> cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = entityManager.createQuery(cq);
            List<User> users =  q.getResultList();
            return users.stream()
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
    public Employee findEmployeeByFullName(EmployeeDTO employee) {
        return entityManager.createQuery(
                        "SELECT e FROM Employee e " +
                                "WHERE e.name = :name AND e.lastName = :lastName AND e.email = :email",
                        Employee.class
                )
                .setParameter("name", employee.name())
                .setParameter("lastName", employee.lastName())
                .setParameter("email", employee.email())
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourcesNotFoundException(
                        "Error al Buscar Empleado",
                        "El empleado no ha sido encontrado en los recursos disponibles."
                ));
    }

    @Override
    public Role findRoleByName(String role)
    {
        return entityManager.createQuery(
                        "SELECT e FROM Role e " +
                                "WHERE e.name = :role",
                        Role.class
                )
                .setParameter("role", role)
                .getSingleResult();
    }

    @Override
    public boolean isUsernameExist(UUID id, String username)
    {
       try
       {
           entityManager = HibernateUtils.getEntityManager();
           if (id == null)
           {
               TypedQuery<Long> query = entityManager.createQuery(
                       "SELECT COUNT(u) FROM User u " +
                               "WHERE u.username = :username",
                       Long.class
               );
               query.setParameter("username", username);
               return query.getSingleResult() > 0;
           }
           TypedQuery<Long> query = entityManager.createQuery(
                   "SELECT COUNT(u) FROM User u " +
                           "WHERE u.username = :username AND u.employee.id <> :id",
                   Long.class
           );
           query.setParameter("username", username);
           query.setParameter("id", id);
           return query.getSingleResult() > 0;
       }
       catch (NoResultException e)
       {
           JOptionPane.showMessageDialog(
                   pageManager,
                  "No se encontraron resultados",
                   "Error al Buscar Usuario",
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
}
