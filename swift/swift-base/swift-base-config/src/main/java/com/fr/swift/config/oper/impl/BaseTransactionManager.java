package com.fr.swift.config.oper.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.proxy.SessionInvocationHandler;

import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-28
 */
public abstract class BaseTransactionManager implements TransactionManager {
    @Override
    public <T> T doTransactionIfNeed(TransactionWorker<T> worker) throws SQLException {
        Object object = createSession();
        ConfigSession session = (ConfigSession) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{ConfigSession.class}, new SessionInvocationHandler(object));
        if (worker.needTransaction()) {
            ConfigTransaction tx = session.beginTransaction();
            tx.begin();
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

    protected abstract Object createSession();
}
