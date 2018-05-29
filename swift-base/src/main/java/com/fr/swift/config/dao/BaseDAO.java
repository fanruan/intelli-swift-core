package com.fr.swift.config.dao;

import com.fr.config.dao.HibernateTemplate;
import com.fr.stable.db.DBSession;
import com.fr.store.access.AccessActionCallback;
import com.fr.store.access.ResourceHolder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;
import com.fr.third.org.hibernate.Query;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public class BaseDAO<T> implements SwiftConfigDAO<T> {
    protected final SwiftLogger LOGGER = SwiftLoggers.getLogger(this.getClass());

    private Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean saveOrUpdate(final T entity) throws SQLException {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
                public Boolean doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();

                    try {
                        session.merge(entity);
                    } catch (Exception e) {
                        return Crasher.crash("save failed", e);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public T select(final String id) throws SQLException {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<T>() {
                public T doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();
                    T result = null;

                    try {
                        result = session.findOneById(entityClass, id);
                        return result;
                    } catch (Exception e) {
                        return Crasher.crash("cannot execute query", e);
                    }
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public List<T> find(final String hql) {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<List<T>>() {
                public List<T> doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();
                    List result = null;

                    try {
//                    Query query = session.createHibernateQuery("from T entity where entity.id like '" + hql + "%'");
                        Query query = session.createHibernateQuery(hql);
                        result = query.list();
                        return result;
                    } catch (Exception e) {
                        return Crasher.crash("query failed", e);
                    }
                }
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<T> find() {
        return find("from " + entityClass.getSimpleName() + " entity");
    }

    public boolean deleteById(final String id) throws SQLException {
        try {
            return HibernateTemplate.getInstance().doQuery(new AccessActionCallback<Boolean>() {
                public Boolean doInServer(ResourceHolder holder) {
                    DBSession session = (DBSession) holder.getResource();

                    try {
                        Query query = session.createHibernateQuery("delete from " + entityClass.getSimpleName() + " entity where entity.id='" + id + "'");
                        query.executeUpdate();
                    } catch (Exception e) {
                        return Crasher.crash("deleteById failed", e);
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
