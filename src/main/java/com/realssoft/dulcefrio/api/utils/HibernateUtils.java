package com.realssoft.dulcefrio.api.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernateUtils
{

    private static final String PERSISTENCE_UNIT_NAME = "dulceFrioPU";
    private static final EntityManagerFactory entityManagerFactory;

    static
    {
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static EntityManager getEntityManager()
    {
        return entityManagerFactory.createEntityManager();
    }

}
