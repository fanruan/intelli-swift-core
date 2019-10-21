package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.TransactionManager;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-28
 */
public abstract class BaseTransactionManager implements TransactionManager {
    @Override
    public <T> T doTransactionIfNeed(TransactionWorker<T> worker) throws SQLException {
        try {
            ConfigSession session = createSession();
            if (worker.needTransaction()) {
                ConfigTransaction tx = session.beginTransaction();
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
        } catch (ClassNotFoundException e) {
            throw new SQLException("create db session error", e);
        }

    }

    protected abstract ConfigSession createSession() throws ClassNotFoundException;
}
