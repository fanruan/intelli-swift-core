package com.fr.swift.config.hibernate;

import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.third.org.hibernate.Criteria;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-30
 */
public class HibernateQuery<T> implements ConfigQuery<T> {

    private Criteria criteria;

    public HibernateQuery(Class<T> entity, Session session) {
        this.criteria = session.createCriteria(entity);
    }

    @Override
    public List<T> executeQuery() {
        return criteria.list();
    }

    @Override
    public void where(ConfigWhere... wheres) {
        for (ConfigWhere where : wheres) {
            switch (where.type()) {
                case LIKE:
                    criteria.add(Restrictions.like(where.getColumn(), where.getValue()));
                    break;
                case IN:
                    criteria.add(Restrictions.in(where.getColumn(), (Collection) where.getValue()));
                    break;
                case EQ:
                    criteria.add(Restrictions.eq(where.getColumn(), where.getValue()));
                    break;
                default:
            }
        }
    }

    @Override
    public void orderBy(Order... orders) {
        for (Order order : orders) {
            if (order.isAsc()) {
                criteria.addOrder(com.fr.third.org.hibernate.criterion.Order.asc(order.getColumn()));
            } else {
                criteria.addOrder(com.fr.third.org.hibernate.criterion.Order.desc(order.getColumn()));
            }
        }

    }

    @Override
    public int executeUpdate() {
        return 0;
    }
}
