package com.fr.swift.decision.config.base;

import com.fr.config.Configuration;
import com.fr.transaction.Worker;

/**
 * @author yee
 * @date 2018/6/15
 */
public abstract class FRConfTransactionWorker implements Worker {
    private Class<? extends Configuration>[] targets;

    public FRConfTransactionWorker(Class<? extends Configuration>[] targets) {
        this.targets = targets;
    }

    @Override
    public Class<? extends Configuration>[] targets() {
        return targets;
    }
}