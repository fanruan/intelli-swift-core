package com.fr.swift.cloud.config.dao;

import com.fr.swift.cloud.config.HibernateManager;
import org.hibernate.SessionFactory;

/**
 * @author anchore
 * @date 2019/12/26
 */
public class SwiftDaoImpl<T> extends AbstractSwiftDao<T> {

    public SwiftDaoImpl(Class<?> entityClass) {
        this(HibernateManager.INSTANCE.getFactory(), entityClass);
    }

    public SwiftDaoImpl(SessionFactory sessionFactory, Class<?> entityClass) {
        super(sessionFactory, entityClass);
    }
}