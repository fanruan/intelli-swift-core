package com.fr.swift.config.hibernate.transaction;

/**
 * @author yee
 * @date 2018/6/30
 */
public abstract class AbstractTransactionWorker<T> implements HibernateTransactionManager.HibernateWorker<T> {
    private boolean needTx;

    public AbstractTransactionWorker() {
        this(true);
    }

    public AbstractTransactionWorker(boolean needTx) {
        this.needTx = needTx;
    }

    @Override
    public boolean needTransaction() {
        return needTx;
    }

}
