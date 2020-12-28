package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.DeleteProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.base.node.ClusterNode;
import com.fr.swift.db.Where;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.local.LocalUrl;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author lucifer
 * @date 2020/3/18
 * @description
 * @since swift 1.1
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(DeleteProcessHandler.class)
public class SwiftDeleteProcessHandler extends BaseProcessHandler implements DeleteProcessHandler {

    public SwiftDeleteProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }


    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        SourceKey tableKey = (SourceKey) args[0];
        Where where = (Where) args[1];
        final Class<?> proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        List<URL> urls = processUrl(targets);
        List<Pair<URL, Future>> futures = new ArrayList<>();
        for (URL url : urls) {
            try {
                Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
                RpcFuture<?> rpcFuture = (RpcFuture<?>) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
                futures.add(new Pair<>(url, rpcFuture));
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn(e);
                backupDeletion(tableKey, where, url);
            }
        }
        nodeContainer.getOfflineNodes().forEach((k, v) -> backupDeletion(tableKey, where, k));
        for (Pair<URL, Future> future : futures) {
            try {
                future.getValue().get();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                backupDeletion(tableKey, where, future.getKey());
            }
        }
        return true;
    }

    @Override
    protected List<URL> processUrl(Target[] targets, Object... args) {
        Map<String, ClusterNode> onlineNodes = nodeContainer.getOnlineNodes();
        List<URL> urls = onlineNodes.keySet().stream().map(id -> UrlSelector.getInstance().getFactory().getURL(id)).collect(Collectors.toList());
        return urls.isEmpty() ? Collections.singletonList(new LocalUrl()) : urls;
    }

    /**
     * if is not self,then backup delete;
     *
     * @param sourceKey
     * @param where
     * @param url
     */
    protected void backupDeletion(SourceKey sourceKey, Where where, URL url) {
        ExecutorTaskService executorTaskService = SwiftContext.get().getBean(ExecutorTaskService.class);
        try {
            if (!isSelf(url)) {
                executorTaskService.save(DeleteExecutorTask.ofByCluster(sourceKey, where, url.getDestination().getId()));
            }
        } catch (Exception ignore) {
            SwiftLoggers.getLogger().warn(ignore);
        }
    }

    protected void backupDeletion(SourceKey sourceKey, Where where, String clusterId) {
        backupDeletion(sourceKey, where, UrlSelector.getInstance().getFactory().getURL(clusterId));
    }

    private boolean isSelf(URL url) {
        boolean isNull = (url == null || url.getDestination() == null || url.getDestination().getId() == null);
        return isNull || url.getDestination().getId().equals(SwiftProperty.get().getMachineId());
    }
}