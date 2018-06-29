package com.fr.swift.config.hibernate.transaction;

import com.fr.swift.config.hibernate.HibernateManager;

/**
 * @author yee
 * @date 2018/6/30
 */
public abstract class AbstractTransactionWorker<T> implements HibernateManager.HibernateWorker<T> {
    @Override
    public boolean needTransaction() {
        return true;
    }

}
