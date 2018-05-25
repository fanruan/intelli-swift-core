package com.fr.swift.config.conf.transaction;

import com.fr.swift.config.conf.ConfigType;
import com.fr.swift.config.conf.dao.SwiftConfigDAO;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface TransactionWorker<T extends SwiftConfigDAO> {
    Object work(T dao);

    ConfigType type();

    boolean needTransaction();
}
