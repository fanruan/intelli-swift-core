package com.fr.swift.config.convert.hibernate.transaction;

import com.fr.swift.config.convert.hibernate.HibernateManager;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.Transaction;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/30
 */
@Service
public class HibernateTransactionManager {

    @Autowired
    private HibernateManager hibernateManager;

    public <T> T doTransactionIfNeed(HibernateWorker<T> worker) throws SQLException {
        Session session = hibernateManager.getFactory().openSession();
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
