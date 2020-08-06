package com.fr.swift.config.dao;

import com.fr.swift.config.HibernateManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    public void deleteQuery(CriteriaQueryProcessor criteriaQueryProcessor) {
        delete((List<T>) selectQuery(criteriaQueryProcessor));
    }

    @Override
    public void update(T entity) {
        update(Collections.singletonList(entity));
    }

    @Override
    public void update(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                for (T entity : entities) {
                    session.update(entity);
                }
                tx.commit();
            } catch (Throwable e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<?> selectQuery(CriteriaQueryProcessor criteriaQueryProcessor) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery query = criteriaBuilder.createQuery(entityClass);
            Root from = query.from(entityClass);
            if (criteriaQueryProcessor != null) {
                criteriaQueryProcessor.process(query, criteriaBuilder, from);
            }
            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public List<?> selectAll() {
        return selectQuery(null);
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