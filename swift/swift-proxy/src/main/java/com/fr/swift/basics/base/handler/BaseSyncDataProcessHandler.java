package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.SyncDataProcessHandler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * TODO 实现
 *
 * @author yee
 * @date 2018/10/30
 */
public abstract class BaseSyncDataProcessHandler<T> extends BaseProcessHandler<T> implements SyncDataProcessHandler {

    public BaseSyncDataProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        return super.processResult(method, targets, args);
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return null;
    }

    @Override
    public T processUrl(Target[] targets, Object... args) {
        // TODO 获取history地址
        return null;
    }
}
