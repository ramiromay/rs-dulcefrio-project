package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.exception.NoChangesException;
import com.realssoft.dulcefrio.api.exception.ResourcesNotFoundException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.ShoppingCartMapper;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.model.dto.ShoppingCartDTO;
import com.realssoft.dulcefrio.api.persistence.dao.EmployeeDao;
import com.realssoft.dulcefrio.api.persistence.dao.ShoppingCartDao;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Product;
import com.realssoft.dulcefrio.api.persistence.entity.ShoppingCart;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.HibernateException;

import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShoppingCartDaoImpl implements ShoppingCartDao
{

    private static final ShoppingCartDaoImpl INSTANCE = new ShoppingCartDaoImpl();
    private static Window pageManager;
    private final Mapper<ShoppingCart, ShoppingCartDTO> mapper = ShoppingCartMapper.getInstance();
    private EntityManager entityManager;

    private ShoppingCartDaoImpl() {}

    public static ShoppingCartDaoImpl getInstance(Window w)
    {
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

    private void getProductAndRemove(String name, Integer newNumberProduct)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            entityManager1.getTransaction().begin();
            TypedQuery<Product> query = entityManager1.createQuery(
                    "SELECT c FROM Product c WHERE c.name = :name",
                    Product.class
            );
            query.setParameter("name", name);
            Product  product = query.getSingleResult();
            product.setStock(product.getStock() + newNumberProduct);
            entityManager1.getTransaction().commit();

        }
        catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

    }

    private Product getProductAndRemove(ShoppingCartDTO entity)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            entityManager1.getTransaction().begin();
            TypedQuery<Product> query = entityManager1.createQuery(
                    "SELECT c FROM Product c WHERE c.name = :name",
                    Product.class
            );
            query.setParameter("name", entity.product().name());
            Product  product = query.getSingleResult();
            product.setStock(product.getStock() + entity.quantity());
            entityManager1.getTransaction().commit();
            return product;
        }
        catch (IllegalStateException ex) {
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


    private Product getProductAndAdd(ShoppingCartDTO entity)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            entityManager1.getTransaction().begin();
            Product product = entityManager1.find(Product.class, entity.product().id());
            product.setStock(product.getStock() - entity.quantity());
            entityManager1.getTransaction().commit();
            return product;
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

    @Transactional
    @Override
    public UUID save(ShoppingCartDTO entity) {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }


            if (isExisted(entity.product().name()))
            {
                getProductAndAdd(entity);
                ShoppingCart foundProduct = getProductOfShoppingCart(entity.product().name());
                entityManager = HibernateUtils.getEntityManager();
                entityManager.getTransaction().begin();
                foundProduct.setNumberProduct(foundProduct.getNumberProduct() + entity.quantity());
                entityManager.merge(foundProduct);
                entityManager.getTransaction().commit();
                return foundProduct.getId();
            }

            ShoppingCart newProduct = mapper.toEntity(entity);
            entityManager = HibernateUtils.getEntityManager();
            entityManager.getTransaction().begin();

            Product  product = getProductAndAdd(entity);
            UUID employeeId = getEmployeeId();
            Employee employee = getEmployee(employeeId);

            newProduct.setEmployee(employee);
            newProduct.setProduct(product);
            entityManager. persist(newProduct);
            entityManager.getTransaction().commit();
            return newProduct.getId();
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
    public UUID update(UUID id, ShoppingCartDTO entity) {
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
            ShoppingCart foundProduct = entityManager.find(ShoppingCart.class, id);
            entityManager.getTransaction().begin();
            if (foundProduct == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Modificar Empleado",
                        "No se puede realizar la modificación. El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            int oldNumberProduct = foundProduct.getNumberProduct();
            int newNumberProduct = entity.quantity();
            Integer difNumberProduct = oldNumberProduct - newNumberProduct;
            getProductAndRemove(entity.product().name(), difNumberProduct);
            foundProduct.setNumberProduct(entity.quantity());
            entityManager.getTransaction().commit();
            return foundProduct.getId();
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

    @Override
    public void delete(UUID id) {
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
            ShoppingCart employee = entityManager.getReference(ShoppingCart.class, id);
            getProductAndRemove(employee.getProduct().getName(), employee.getNumberProduct());
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
    public ShoppingCartDTO findById(UUID id) {
        return null;
    }

    public List<ShoppingCartDTO> findAll() {
        EntityManager entityManager1 = null;
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            User user = entityManager.find(User.class, Main.ID_USER);

            entityManager1 = HibernateUtils.getEntityManager();
            TypedQuery<ShoppingCart> query = entityManager1.createQuery(
                    "SELECT c FROM ShoppingCart c WHERE c.employee.id = :id",
                    ShoppingCart.class
            );
            query.setParameter("id", user.getEmployee().getId());
            return query.getResultList()
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        }
        catch (IllegalStateException ex)
        {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        finally {
            close();
            entityManager1.close();
        }
        return Collections.emptyList();
    }

    private boolean isExisted(String name)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            TypedQuery<Long> query = entityManager1.createQuery(
                    "SELECT COUNT(c) FROM ShoppingCart c WHERE c.product.name = :name",
                    Long.class
            );
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        return false;

    }

    private ShoppingCart getProductOfShoppingCart(String name)
    {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            TypedQuery<ShoppingCart> query = entityManager1.createQuery(
                    "SELECT c FROM ShoppingCart c WHERE c.product.name = :name",
                    ShoppingCart.class
            );
            query.setParameter("name", name);
            return query.getSingleResult();
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
    public boolean isEnoughProduct(String name, Integer numberProduct) {
        try (EntityManager entityManager1 = HibernateUtils.getEntityManager()) {
            TypedQuery<Integer> query = entityManager1.createQuery(
                    "SELECT c.stock FROM Product c WHERE c.name = :name",
                    Integer.class
            );
            query.setParameter("name", name);
            return query.getSingleResult() >= numberProduct;
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda de los roles.",
                    StringRS.TITLE_DIALOG_ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
        return false;
    }
}
