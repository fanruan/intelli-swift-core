package com.fr.swift.config.oper;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigSessionCreator {
    <T> T doTransactionIfNeed(TransactionWorker<T> worker) throws SQLException;

    ConfigSession createSession() throws ClassNotFoundException;

    interface TransactionWorker<T> {
        boolean needTransaction();

        T work(ConfigSession session) throws SQLException;
    }
}
