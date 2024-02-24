package com.realssoft.dulcefrio.api.persistence.dao;

import java.util.List;

public interface Dao<E,I>
{

    I save(E entity);
    I update(I id, E entity);
    void delete(I id);
    E findById(I id);
    List<E> findAll();

}
