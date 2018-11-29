package com.fr.swift.config.oper;

/**
 * @author yee
 * @date 2018-11-27
 */
public abstract class BaseTransactionWorker<T> implements TransactionManager.TransactionWorker<T> {
    private boolean needTx = true;

    public BaseTransactionWorker(boolean needTx) {
        this.needTx = needTx;
    }

    public BaseTransactionWorker() {
    }

    @Override
    public boolean needTransaction() {
        return needTx;
    }
}
