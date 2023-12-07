package com.elonewong.onlinecourseapi.csr.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CanGetEntityManagerImpl implements CanGetEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

}
