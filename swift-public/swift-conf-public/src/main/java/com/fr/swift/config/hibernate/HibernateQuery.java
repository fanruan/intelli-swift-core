package com.fr.swift.config.hibernate;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Expression;
import com.fr.swift.config.oper.Order;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-30
 */
public class HibernateQuery<T> implements ConfigQuery<T> {
    private CriteriaQuery<T> criteriaQuery;
    private CriteriaBuilder builder;
    private Session session;
    private Root<T> root;

    public HibernateQuery(Class<T> entity, Session session) {
        this.session = session;
        this.builder = this.session.getCriteriaBuilder();
        this.criteriaQuery = builder.createQuery(entity);
        this.root = this.criteriaQuery.from(entity);
    }

    @Override
    public List<T> executeQuery() {
        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void where(ConfigWhere... wheres) {
        List<Predicate> list = new ArrayList<Predicate>();
        for (ConfigWhere where : wheres) {
            Path path = getPath(where);
            switch (where.type()) {
                case LIKE:
                    list.add(builder.like(path, (String) where.getValue()));
                    break;
                case IN:
                    Predicate predicate = builder.in(path).getExpression().in((Collection<?>) where.getValue());
                    list.add(predicate);
                    break;
                case EQ:
                    list.add(builder.equal(path, where.getValue()));
                    break;
                default:
            }
        }
        if (!list.isEmpty()) {
            criteriaQuery.where(list.toArray(new Predicate[0]));
        }
    }

    @Override
    public void orderBy(Order... orders) {
        List<javax.persistence.criteria.Order> list = new ArrayList<javax.persistence.criteria.Order>();
        for (Order order : orders) {
            Path path = getPath(order);
            if (order.isAsc()) {
                list.add(builder.asc(path));
            } else {
                list.add(builder.desc(path));
            }
        }

        if (!list.isEmpty()) {
            criteriaQuery.orderBy(list);
        }
    }

    @Override
    public int executeUpdate() {
        return 0;
    }

    private Path getPath(Expression order) {
        Path path = null;
        String column = order.getColumn();
        String[] properties = column.split("\\.");
        for (String property : properties) {
            if (null == path) {
                path = root.get(property);
            } else {
                path = path.get(property);
            }
        }
        return path;
    }
}
