package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.SaleMapper;
import com.realssoft.dulcefrio.api.model.dto.SaleDTO;
import com.realssoft.dulcefrio.api.model.dto.TicketDTO;
import com.realssoft.dulcefrio.api.persistence.dao.SaleDao;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Product;
import com.realssoft.dulcefrio.api.persistence.entity.Sale;
import com.realssoft.dulcefrio.api.persistence.entity.Ticket;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.List;
import java.util.UUID;

public class SaleDaoImpl implements SaleDao
{

    private static final SaleDaoImpl INSTANCE = new SaleDaoImpl();
    private static Window pageManager;
    private final Mapper<Sale, SaleDTO> mapper = SaleMapper.getInstance();
    private EntityManager entityManager;

    private SaleDaoImpl() {}

    public static SaleDaoImpl getInstance(Window w) {
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

    private Ticket getTicket(UUID id)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            return entityManager1.find(Ticket.class, id);
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

    private Product getProduct(UUID id)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            return entityManager1.find(Product.class, id);
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
    public UUID save(SaleDTO entity) {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }
            Product product = getProduct(entity.product().id());
            Ticket ticket = getTicket(entity.ticket().id());
            Sale newEmployee = mapper.toEntity(entity);
            newEmployee.setProduct(product);
            newEmployee.setTicket(ticket);
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

    public List<SaleDTO> findByTicket(UUID id)
    {
        try (EntityManager  entityManager1 = HibernateUtils.getEntityManager()) {
            entityManager = HibernateUtils.getEntityManager();
            TypedQuery<Sale> query = entityManager.createQuery(
                    "SELECT e FROM Sale e" +
                            " WHERE e.ticket.id = :id",
                    Sale.class
            );
            query.setParameter("id", id);
            return query.getResultList()
                    .stream()
                    .map(mapper::toDto).toList();
        }
    }

    @Override
    public UUID update(UUID id, SaleDTO entity) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public SaleDTO findById(UUID id) {
        return null;
    }

    @Override
    public List<SaleDTO> findAll() {
        return null;
    }
}
