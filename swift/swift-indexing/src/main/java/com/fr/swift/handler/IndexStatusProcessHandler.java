package com.fr.swift.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.StatusProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.util.Crasher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018-12-01
 */
@SwiftBean
@SwiftScope("prototype")
public class IndexStatusProcessHandler extends BaseProcessHandler<List<URL>> implements StatusProcessHandler {
    public IndexStatusProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(final Method method, Target[] targets, final Object... args) throws Throwable {
        List<URL> urls = processUrl(targets, args);
        if (ClusterSelector.getInstance().getFactory().isCluster() && urls.isEmpty()) {
            Crasher.crash("Remote  Not Found");
        }
        FindList<RpcFuture> list = new FindListImpl<RpcFuture>(urls);
        final Class proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        List<RpcFuture> resultList = list.forEach(new FindList.ConvertEach<URL, RpcFuture>() {
            @Override
            public RpcFuture forEach(int idx, URL item) throws Exception {
                Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, item);
                try {
                    return (RpcFuture) invoke(invoker, proxyClass, method, method.getName(), parameterTypes, args);
                } catch (Throwable throwable) {
                    throw new Exception(throwable);
                }
            }
        });
        return mergeResult(resultList);
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        final List<ServerCurrentStatus> statusList = new ArrayList<ServerCurrentStatus>();
        for (Object obj : resultList) {
            if (obj instanceof RpcFuture) {
                RpcFuture future = (RpcFuture) obj;
                final CountDownLatch latch = new CountDownLatch(1);
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        if (result instanceof ServerCurrentStatus) {
                            statusList.add((ServerCurrentStatus) result);
                        }
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        SwiftLoggers.getLogger().warn(e.getMessage());
                        latch.countDown();
                    }
                });
                latch.await();
            } else if (obj instanceof ServerCurrentStatus) {
                statusList.add((ServerCurrentStatus) obj);
            }
        }
        Collections.sort(statusList);
        return statusList.get(0);
    }

    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        Map<String, ClusterEntity> entityMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
        Set<String> clusterIds = entityMap.keySet();
        List<URL> urls = new ArrayList<URL>();
        for (String clusterId : clusterIds) {
            urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
        }
        return Collections.unmodifiableList(urls);
    }
}
