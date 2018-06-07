package com.fr.swift.config.transaction;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/7
 */
public abstract class AbstractTransactionWorker implements TransactionWorker {
    @Override
    public abstract Object work() throws SQLException;

    @Override
    public boolean needTransaction() {
        return true;
    }
}
