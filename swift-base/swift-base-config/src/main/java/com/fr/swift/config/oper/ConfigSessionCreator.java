package com.fr.swift.config.oper;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigSessionCreator {
    /**
     * 创建session
     *
     * @return
     * @throws ClassNotFoundException
     */
    ConfigSession createSession() throws ClassNotFoundException;

    /**
     * @param worker
     * @param <T>
     * @return
     * @throws SQLException
     * @deprecated 弃用，去掉service后删
     */
    @Deprecated
    <T> T doTransactionIfNeed(TransactionWorker<T> worker) throws SQLException;

    /**
     * @deprecated 弃用  去掉service后删除
     * @param <T>
     */
    interface TransactionWorker<T> {
        boolean needTransaction();

        T work(ConfigSession session) throws SQLException;
    }
}
