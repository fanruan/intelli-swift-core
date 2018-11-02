package com.fr.swift.process.handler;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class NodesProcessHandler extends AbstractProcessHandler {

    public NodesProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();

        List<NodeState> nodeStateList = (List<NodeState>) args[0];
        for (NodeState nodeState : nodeStateList) {
            if (nodeState.getNodeType() == NodeType.ONLINE) {
                Invoker invoker = invokerCreater.createInvoker(proxyClass, UrlSelector.getInstance().getFactory().getURL(nodeState.getHeartBeatInfo().getNodeId()));
                handleAsyncResult(invoke(invoker, proxyClass, method, methodName, parameterTypes, args));
            }
        }
        MonitorUtil.finish(methodName);
        return null;
    }

    @Override
    public List<URL> processUrl(Target target) {
        return null;
    }
}
