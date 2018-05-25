package com.fr.swift.config.transaction;

import com.fr.swift.config.ConfigType;
import com.fr.swift.config.dao.SwiftSegmentDAO;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
public abstract class SwiftSegmentTransactionWorker implements TransactionWorker<SwiftSegmentDAO> {

    @Override
    public ConfigType type() {
        return ConfigType.SEGMENT;
    }

    @Override
    public abstract Object work(SwiftSegmentDAO dao) throws SQLException;

    @Override
    public boolean needTransaction() {
        return true;
    }
}
