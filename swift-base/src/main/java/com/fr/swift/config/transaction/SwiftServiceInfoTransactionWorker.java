package com.fr.swift.config.transaction;

import com.fr.swift.config.ConfigType;
import com.fr.swift.config.dao.SwiftServiceInfoDao;

import java.sql.SQLException;

/**
 * This class created on 2018/5/31
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class SwiftServiceInfoTransactionWorker implements TransactionWorker<SwiftServiceInfoDao> {

    @Override
    public abstract Object work(SwiftServiceInfoDao dao) throws SQLException;

    @Override
    public ConfigType type() {
        return ConfigType.SERVICE;
    }

    @Override
    public boolean needTransaction() {
        return true;
    }
}
