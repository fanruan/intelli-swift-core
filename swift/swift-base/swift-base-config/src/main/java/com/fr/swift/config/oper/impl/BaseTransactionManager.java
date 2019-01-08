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
        ConfigSession session = createSession();
        if (worker.needTransaction()) {
            ConfigTransaction tx = session.beginTransaction();
            // 不需要begin因为上面已经begin了
//            tx.begin();
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

    protected abstract ConfigSession createSession();
}
