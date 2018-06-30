package com.fr.swift.config.hibernate.transaction;

import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.SessionFactory;
import com.fr.third.org.hibernate.Transaction;
import com.fr.third.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/30
 */
@Service
public class HibernateTransactionManager {

    @Resource(name = "swiftConfigSessionFactory")
    private SessionFactory sessionFactory;

    public <T> T doTransactionIfNeed(HibernateWorker<T> worker) throws SQLException {
        Session session = sessionFactory.openSession();
        if (worker.needTransaction()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = worker.work(session);
                tx.commit();
                return result;
            } catch (Throwable throwable) {
                tx.rollback();
                throw new SQLException(throwable);
            } finally {
                session.close();
            }
        } else {
            try {
                return worker.work(session);
            } finally {
                session.close();
            }
        }
    }

    public interface HibernateWorker<T> {
        boolean needTransaction();

        T work(Session session) throws SQLException;
    }
}
