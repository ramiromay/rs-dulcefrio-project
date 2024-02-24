package com.realssoft.dulcefrio.api.persistence.dao.impl;

import com.realssoft.dulcefrio.api.exception.InvalidArgumentException;
import com.realssoft.dulcefrio.api.exception.NoChangesException;
import com.realssoft.dulcefrio.api.exception.ResourcesNotFoundException;
import com.realssoft.dulcefrio.api.mapper.Mapper;
import com.realssoft.dulcefrio.api.mapper.impl.CategoryMapper;
import com.realssoft.dulcefrio.api.mapper.impl.ProductMapper;
import com.realssoft.dulcefrio.api.model.dto.CategoryDTO;
import com.realssoft.dulcefrio.api.model.dto.ProductDTO;
import com.realssoft.dulcefrio.api.persistence.dao.ProductDao;
import com.realssoft.dulcefrio.api.persistence.entity.Category;
import com.realssoft.dulcefrio.api.persistence.entity.CategoryType;
import com.realssoft.dulcefrio.api.persistence.entity.Employee;
import com.realssoft.dulcefrio.api.persistence.entity.Product;
import com.realssoft.dulcefrio.api.persistence.entity.User;
import com.realssoft.dulcefrio.api.utils.HibernateUtils;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.List;
import java.util.UUID;

public class ProductDaoImpl implements ProductDao
{

    private static final ProductDaoImpl INSTANCE = new ProductDaoImpl();
    private static Window pageManager;
    private final Mapper<Product, ProductDTO> mapper = ProductMapper.getInstance();

    private EntityManager entityManager;

    private ProductDaoImpl() {}

    public static ProductDaoImpl getInstance(Window w)
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

    @Override
    public UUID save(ProductDTO entity) {
        try
        {
            if (entity == null)
            {
                throw new InvalidArgumentException(
                        "Error al Registrar Empleado",
                        "No se pudo realizar el registro del empleado debido a datos no válidos."
                );
            }
            Product newProduct = mapper.toEntity(entity);
            entityManager = HibernateUtils.getEntityManager();
            entityManager.getTransaction().begin();
            Category category = findCategoryByName(entity.category().name());
            newProduct.setCategory(category);
            entityManager.persist(newProduct);
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
    public UUID update(UUID id, ProductDTO entity) {
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
            Product foundProduct = entityManager.find(Product.class, id);
            Category foundCategory = findCategoryByName(entity.category().name());

            if (foundProduct == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Modificar Empleado",
                        "No se puede realizar la modificación. El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            if (foundCategory == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Modificar pro",
                        "No se puede realizar la modificación. El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            entityManager.getTransaction().begin();
            foundProduct.setName(entity.name());
            foundProduct.setPrice(entity.price());
            foundProduct.setStock(entity.stock());
            foundProduct.setAvailable(entity.isAvailable());
            foundProduct.setCategory(foundCategory);
            entityManager.getTransaction().commit();
            return foundProduct.getId();
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
            Product product = entityManager.getReference(Product.class, id);
            entityManager.remove(product);
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
    public ProductDTO findById(UUID id)
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
            Product foundProduct = entityManager.find(Product.class, id);
            if (foundProduct == null)
            {
                throw new ResourcesNotFoundException(
                        "Error al Buscar Empleado",
                        "El empleado no ha sido encontrado en los recursos disponibles."
                );
            }
            return mapper.toDto(foundProduct);
        }
        catch (InvalidArgumentException | ResourcesNotFoundException | EntityNotFoundException | HibernateException e)
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
    public List<ProductDTO> findAll() {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            CriteriaQuery<Object> cq = entityManager.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
            Query q = entityManager.createQuery(cq);
            List<Product> employees =  q.getResultList();
            return employees.stream()
                    .map(mapper::toDto)
                    .toList();
        }
        catch (IllegalStateException | PersistenceException ex)
        {
            JOptionPane.showMessageDialog(
                    pageManager,
                    "No se pudo realizar la busqueda del empleado.",
                    StringRS.TITLE_DIALOG_ERROR,
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
    public List<CategoryDTO> findAllCategory()
    {
       try {
           entityManager = HibernateUtils.getEntityManager();
           TypedQuery<Category> query = entityManager.createQuery(
                   "SELECT c FROM Category c",
                   Category.class
           );

           return query.getResultList()
                   .stream()
                   .map(CategoryMapper.getInstance()::toDto)
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
       }
       return Collections.emptyList();
    }

    @Override
    public Category findCategoryByName(String name) {
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT e FROM Category e " +
                        "WHERE e.name = :name",
                Category.class
        );
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public boolean isProductExist(UUID id, String name) {
        try
        {
            entityManager = HibernateUtils.getEntityManager();
            if (id == null)
            {
                TypedQuery<Long> query = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Product e " +
                                "WHERE e.name = :name",
                        Long.class
                );
                query.setParameter("name", name);
                return query.getSingleResult() > 0;
            }

            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(e) FROM Product e " +
                            "WHERE e.name = :name AND e.id <> :id",
                    Long.class
            );
            query.setParameter("name", name);
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

}
