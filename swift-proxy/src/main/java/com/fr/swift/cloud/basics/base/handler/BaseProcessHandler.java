package com.fr.swift.cloud.basics.base.handler;

import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.ProcessHandler;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.cluster.base.node.ClusterNodeContainer;
import com.fr.swift.cloud.cluster.base.selector.ClusterNodeSelector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProcessHandler<T> extends AbstractProcessHandler<T> implements ProcessHandler {

    protected ClusterNodeContainer nodeContainer;

    public BaseProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
        this.nodeContainer = ClusterNodeSelector.getInstance().getContainer();
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        List<Object> result = new ArrayList<Object>();
        return mergeResult(result);
    }

    /**
     * 合并Result
     *
     * @param resultList
     * @param args
     * @return
     */
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        return null;
    }

}
