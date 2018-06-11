package com.fr.swift.config.transaction;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface TransactionWorker {
    Object work() throws SQLException;

    boolean needTransaction();
}
