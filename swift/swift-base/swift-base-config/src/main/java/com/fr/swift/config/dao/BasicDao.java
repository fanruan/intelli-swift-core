package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.converter.ObjectConverter;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/29
 */
public class BasicDao<T extends ObjectConverter> implements SwiftConfigDao<T> {
    protected Class entityClass;
    protected RestrictionFactory factory;

    public BasicDao(Class entityClass, RestrictionFactory factory) {
        this.entityClass = entityClass;
        this.factory = factory;
    }

    @Override
    public boolean saveOrUpdate(ConfigSession session, T entity) throws SQLException {
        session.merge(entity.convert());
        return true;
    }

    @Override
    public void persist(ConfigSession session, T entity) {
        session.save(entity.convert());
    }

    @Override
    public T select(ConfigSession session, Serializable id) throws SQLException {
        try {
            Object result = session.get(entityClass, id);
            if (null != result) {
                return ((ObjectConverter<T>) result).convert();
            }
            return null;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public FindList<T> find(ConfigSession session, Object[] order, Object... criterions) {
        try {
            ConfigCriteria criteria = session.createCriteria(entityClass);
            if (null != order) {
                for (Object order1 : order) {
                    criteria.addOrder(order1);
                }
            }
            if (null != criteria) {
                for (Object criterion : criterions) {
                    criteria.add(criterion);
                }
            }

            final List list = criteria.list();

            return new FindListImpl<T>(list, new FindList.Through<T>() {

                @Override
                public List<T> go() {
                    List<T> result = new ArrayList<T>();
                    for (Object o : list) {
                        result.add(((ObjectConverter<T>) o).convert());
                    }
                    return result;
                }
            });

        } catch (Exception e) {
            return FindList.EMPTY;
        }
    }

    @Override
    public FindList<T> find(ConfigSession session, Object... criteria) {
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
            session.delete(obj.convert());
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return true;
    }
}
