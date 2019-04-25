package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.Page;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class BasicDao<T> implements SwiftConfigDao<T> {
    protected Class entityClass;

    public BasicDao(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public boolean saveOrUpdate(ConfigSession session, T entity) throws SQLException {
        session.merge(entity);
        return true;
    }

    @Override
    public void persist(ConfigSession session, T entity) {
        session.save(entity);
    }

    @Override
    public T select(ConfigSession session, Serializable id) throws SQLException {
        try {
            return (T) session.get(entityClass, id);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<T> find(ConfigSession session, Order[] order, ConfigWhere... criterions) {
        if (session == null) {
            return Collections.emptyList();
        }
        try {
            ConfigQuery query = session.createEntityQuery(entityClass);
            if (null != order && order.length > 0) {
                query.orderBy(order);
            }
            if (null != criterions && criterions.length > 0) {
                query.where(criterions);
            }
            return query.executeQuery();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<T> find(ConfigSession session, ConfigWhere... criteria) {
        return find(session, null, criteria);
    }

    @Override
    public boolean deleteById(ConfigSession session, Serializable id) throws SQLException {
        try {
            Object entity = session.get(entityClass, id);
            if (null != entity) {
                session.delete(entity);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean delete(ConfigSession session, T obj) throws SQLException {
        try {
            session.delete(obj);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return true;
    }

    @Override
    public Page<T> findPage(ConfigSession session, int page, int size, Order[] order, ConfigWhere... criterions) {
        if (session == null) {
            return new Page<T>();
        }

        try {
            ConfigQuery query = session.createEntityQuery(entityClass);
            if (null != order && order.length > 0) {
                query.orderBy(order);
            }
            if (null != criterions && criterions.length > 0) {
                query.where(criterions);
            }

            Page page1 = query.executeQuery(page, size);
            final List list = page1.getData();
            page1.setData(list);
            return page1;
        } catch (Exception e) {
            return new Page<T>();
        }
    }

    @Override
    public Page<T> findPage(ConfigSession session, int page, int size, ConfigWhere... criterion) {
        return findPage(session, page, size, null, criterion);
    }
}
