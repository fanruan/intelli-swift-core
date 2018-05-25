package com.fr.swift.config.conf.transaction;

import com.fr.swift.config.conf.ConfigType;
import com.fr.swift.config.conf.dao.SwiftSegmentDAO;

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
    public abstract Object work(SwiftSegmentDAO dao);

    @Override
    public boolean needTransaction() {
        return true;
    }
}
