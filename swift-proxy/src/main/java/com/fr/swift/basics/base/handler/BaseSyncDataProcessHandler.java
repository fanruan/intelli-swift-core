package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.URL;
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
public abstract class BaseSyncDataProcessHandler extends BaseProcessHandler implements SyncDataProcessHandler {

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        return super.processResult(method, target, args);
    }

    @Override
    protected Object mergeResult(List resultList) {
        return null;
    }

    @Override
    public List<URL> processUrl(Target target) {
        // TODO 获取history地址
        return null;
    }
}
