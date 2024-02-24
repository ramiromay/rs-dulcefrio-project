package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.persistence.entity.CashRegisterOpening;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.InterruptCashRegister;
import com.realssoft.dulcefrio.api.persistence.entity.InterruptType;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.CriteriaQuery;

import javax.swing.JOptionPane;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CashRegisterImpl
{

    private static CashRegisterImpl instance;

    public static CashRegisterImpl getInstance()
    {
        if(instance == null)
        {
            instance = new CashRegisterImpl();
        }
        return instance;
    }

    private CashRegisterImpl() {}

    private UUID getEmployeeId()
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            User user = entityManager1.find(User.class, Main.ID_USER);
            return user.getEmployee().getId();
        } catch (IllegalStateException ex) {

            ex.printStackTrace();
        }
        return null;
    }

    public boolean isOpeningToday()
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager()) {
            Employee employee = getEmployee(getEmployeeId());
            return entityManager.createQuery(
                            "SELECT c FROM CashRegisterOpening c WHERE DATE(c.timestamp) = :start",
                            CashRegisterOpening.class)
                    .setParameter("start", new Date(), TemporalType.DATE)
                    .getResultList().size() > 0;
        } catch (IllegalStateException ex) {
        }
        return false;
    }

    public boolean isClosingToday()
    {
        try (EntityManager entityManager = HibernateUtils.getEntityManager()) {
            Employee employee = getEmployee(getEmployeeId());
            return entityManager.createQuery(
                            "SELECT c FROM InterruptCashRegister c WHERE DATE(c.timestamp) = :end",
                            InterruptCashRegister.class)
                    .setParameter("end", new Date(), TemporalType.DATE)
                    .getResultList().size() > 0;
        } catch (IllegalStateException ex) {
        }
        return false;
    }

    private Employee getEmployee(UUID id)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            return entityManager1.find(Employee.class, id);
        } catch (IllegalStateException ex) {

            ex.printStackTrace();
        }
        return null;
    }


    public UUID opening(CashRegisterOpening entity)
    {
        try(EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }
            Employee newEmployee = getEmployee(getEmployeeId());

            entity.setEmployee(newEmployee);
            entity.setTimestamp(new Date());
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            return entity.getId();
        }
        catch (InvalidArgumentException | IllegalArgumentException | PersistenceException e)
        {

            e.printStackTrace();
        }
        return null;
    }

    public Long getTotalSales(Date start)
    {
        try(EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            return entityManager.createQuery(
                            "SELECT COUNT(c) FROM Ticket c WHERE c.timestamp BETWEEN :start AND :end",
                            Long.class)
                    .setParameter("start", start, TemporalType.TIMESTAMP)
                    .setParameter("end", new Date(), TemporalType.TIMESTAMP)
                    .getSingleResult();
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            return null;
        }
    }

    public InterruptType  getInterruptType(String name)
    {
        try(EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            return entityManager.createQuery(
                            "SELECT c FROM InterruptType c WHERE c.name = :name",
                            InterruptType.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            return null;
        }
    }

    public UUID closing()
    {
        try(EntityManager entityManager = HibernateUtils.getEntityManager())
        {
            InterruptCashRegister entity = new InterruptCashRegister();
            Employee newEmployee = getEmployee(getEmployeeId());
            CashRegisterOpening cashRegisterOpening = getCashRegisterOpening();
            InterruptType  interruptType = getInterruptType("Z");
            Integer totalSales = Integer.parseInt( getTotalSales(cashRegisterOpening.getTimestamp()).toString());
            entity.setSalesTotal(totalSales);
            entity.setCashRegisterOpening(cashRegisterOpening);
            entity.setEmployee(newEmployee);
            entity.setInterruptType(interruptType);
            entity.setTimestamp(new Date());
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            return entity.getId();
        }
        catch (InvalidArgumentException | IllegalArgumentException | PersistenceException e)
        {

            e.printStackTrace();
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isCashRegisterOpen()
    {
        try(EntityManager entityManager = HibernateUtils.getEntityManager())
        {

            Long cashRegisterOpening = entityManager.createQuery(
                            "SELECT COUNT(c) FROM CashRegisterOpening c WHERE DATE(c.timestamp) = :date",
                            Long.class)
                    .setParameter("date", new Date(), TemporalType.DATE)
                    .getSingleResult();

            return cashRegisterOpening == 1;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public CashRegisterOpening getCashRegisterOpening()
    {
        try (EntityManager  entityManager = HibernateUtils.getEntityManager())
        {
            CashRegisterOpening cashRegisterOpening = entityManager.createQuery(
                            "SELECT c FROM CashRegisterOpening c WHERE DATE(c.timestamp) = :date",
                            CashRegisterOpening.class)
                    .setParameter("date", new Date(), TemporalType.DATE)
                    .getSingleResult();
            return cashRegisterOpening;
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            return null;
        }
    }

    public List<InterruptCashRegister> getInterruptCashRegister()
    {
        try (EntityManager  entityManager = HibernateUtils.getEntityManager())
        {
            CriteriaQuery<Object> cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(InterruptCashRegister.class));
            Query q = entityManager.createQuery(cq);
            List<InterruptCashRegister> employees =  q.getResultList();
            if (!isClosingToday())
            {
                if (isOpeningToday())
                {
                    CashRegisterOpening cashRegisterOpening = getCashRegisterOpening();
                    employees.add(InterruptCashRegister.builder()
                            .cashRegisterOpening(cashRegisterOpening)
                            .build());
                }
            }
            return employees;
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            ex.printStackTrace();
        }

        return Collections.emptyList();
    }


}
