package com.fr.swift.config.dao;

import com.fr.third.org.hibernate.Criteria;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.org.hibernate.criterion.Order;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class BasicDao<T> implements SwiftConfigDao<T> {
    protected Class<T> entityClass;

    public BasicDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public boolean saveOrUpdate(Session session, T entity) throws SQLException {
        try {
            session.merge(entity);
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void persist(Session session, T entity) {
        session.persist(entity);
    }

    @Override
    public T select(Session session, Serializable id) throws SQLException {
        try {
            return session.get(entityClass, id);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<T> find(Session session, Order[] order, Criterion... criterions) {
        try {
            Criteria criteria = session.createCriteria(entityClass);
            if (null != order) {
                for (Order order1 : order) {
                    criteria.addOrder(order1);
                }
            }
            if (null != criteria) {
                for (Criterion criterion : criterions) {
                    if (null != criteria) {
                        criteria.add(criterion);
                    }
                }
            }
            return criteria.list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<T> find(Session session, Criterion... criteria) {
        return find(session, null, criteria);
    }

    @Override
    public boolean deleteById(Session session, Serializable id) throws SQLException {
        try {
            T entity = select(session, id);
            if (null != entity) {
                session.delete(entity);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
