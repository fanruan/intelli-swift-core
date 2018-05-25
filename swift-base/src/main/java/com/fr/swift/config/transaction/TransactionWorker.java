package com.fr.swift.config.transaction;

import com.fr.swift.config.ConfigType;
import com.fr.swift.config.dao.SwiftConfigDAO;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface TransactionWorker<T extends SwiftConfigDAO> {
    Object work(T dao) throws SQLException;

    ConfigType type();

    boolean needTransaction();
}
