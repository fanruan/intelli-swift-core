package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.cluster.base.node.ClusterNodeContainer;
import com.fr.swift.cluster.base.selector.ClusterNodeSelector;

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
