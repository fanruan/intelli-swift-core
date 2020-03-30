package com.fr.swift.config.dao;

import com.fr.swift.config.HibernateManager;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2019/12/26
 */
public class SwiftDaoImpl<T> implements SwiftDao<T> {
    private SessionFactory sessionFactory;

    private Class<?> entityClass;

    public SwiftDaoImpl(Class<?> entityClass) {
        this.entityClass = entityClass;
        sessionFactory = HibernateManager.INSTANCE.getFactory();
    }

    @Override
    public void insert(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                for (T entity : entities) {
                    session.save(entity);
                }
                tx.commit();
            } catch (Throwable e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void insertOrUpdate(T entity) {
        insertOrUpdate(Collections.singletonList(entity));
    }

    @Override
    public void insertOrUpdate(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                for (T entity : entities) {
                    session.saveOrUpdate(entity);
                }
                tx.commit();
            } catch (Throwable e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void insert(T entity) {
        insert(Collections.singletonList(entity));
    }

    @Override
    public void delete(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                for (T entity : entities) {
                    session.delete(entity);
                }
                tx.commit();
            } catch (Throwable e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(T entity) {
        delete(Collections.singletonList(entity));
    }

    @Override
    public void delete(CriteriaProcessor criteriaProcessor) {
        delete((List<T>) select(criteriaProcessor));
    }

    @Override
    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.update(entity);
                tx.commit();
            } catch (Throwable e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<?> select(CriteriaProcessor criteriaProcessor) {
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(entityClass);
            if (criteriaProcessor != null) {
                criteriaProcessor.process(criteria);
            }
            return criteria.list();
        }
    }

    @Override
    public List<?> selectAll() {
        return select(null);
    }

    @Override
    public List<?> select(String hql, QueryProcessor queryProcessor) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(hql);
            if (queryProcessor != null) {
                queryProcessor.process(query);
            }
            return query.list();
        }
    }

    @Override
    public List<?> selectAll(String hql) {
        return select(hql, null);
    }
}