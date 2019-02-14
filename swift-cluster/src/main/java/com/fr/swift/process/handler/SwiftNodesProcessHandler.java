package com.fr.swift.process.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.NodesProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(NodesProcessHandler.class)
public class SwiftNodesProcessHandler extends AbstractProcessHandler implements NodesProcessHandler {

    public SwiftNodesProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    /**
     * 同步所有NodeState信息
     *
     * @param method
     * @param targets
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        try {
            MonitorUtil.start();
            List<URL> urlList = processUrl(targets, args);

            final List<EventResult> resultList = new ArrayList<EventResult>();
            final CountDownLatch latch = new CountDownLatch(urlList.size());
            for (final URL url : urlList) {
                Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
                RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
                rpcFuture.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        try {
                            EventResult eventResult = new EventResult(url.getDestination().getId(), true);
                            resultList.add(eventResult);
                        } finally {
                            latch.countDown();
                        }
                    }

                    @Override
                    public void fail(Exception e) {
                        try {
                            EventResult eventResult = new EventResult(url.getDestination().getId(), false);
                            eventResult.setError(e.getMessage());
                            resultList.add(eventResult);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            latch.await();
            return resultList;
        } finally {
            MonitorUtil.finish(methodName);
        }
    }

    /**
     * 根据nodestate算出所有online节点的url
     *
     * @param targets
     * @param args   List<NodeState>
     * @return
     */
    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        List<NodeState> nodeStateList = (List<NodeState>) args[0];
        List<URL> urlList = new ArrayList<URL>();
        for (NodeState nodeState : nodeStateList) {
            if (nodeState.getNodeType() == NodeType.ONLINE) {
                urlList.add(UrlSelector.getInstance().getFactory().getURL(nodeState.getHeartBeatInfo().getNodeId()));
            }
        }
        return urlList;
    }
}
