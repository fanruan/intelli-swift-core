package com.fr.swift.config.dao;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Criteria;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class BasicDao<T> implements SwiftConfigDao<T> {
    protected final SwiftLogger LOGGER = SwiftLoggers.getLogger(this.getClass());
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
    public T select(Session session, String id) throws SQLException {
        try {
            return session.load(entityClass, id);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<T> find(Session session, Criterion... criterions) {
        try {
            Criteria criteria = session.createCriteria(entityClass);

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
    public boolean deleteById(Session session, String id) throws SQLException {
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
