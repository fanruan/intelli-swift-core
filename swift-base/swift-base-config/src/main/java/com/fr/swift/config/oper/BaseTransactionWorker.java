package com.fr.swift.config.oper;

/**
 * @deprecated 弃用 去掉service后删
 * @author yee
 * @date 2018-11-27
 */
@Deprecated
public abstract class BaseTransactionWorker<T> implements ConfigSessionCreator.TransactionWorker<T> {
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
