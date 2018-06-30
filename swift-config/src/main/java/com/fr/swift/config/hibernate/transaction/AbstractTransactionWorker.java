package com.fr.swift.config.hibernate.transaction;

/**
 * @author yee
 * @date 2018/6/30
 */
public abstract class AbstractTransactionWorker<T> implements HibernateTransactionManager.HibernateWorker<T> {
    @Override
    public boolean needTransaction() {
        return true;
    }

}
