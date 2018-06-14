package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.entity.ClusterEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    IndexingSelectRule DEFAULT = new IndexingSelectRule() {
        /**
         * 获取所有索引节点当前运行状态
         * 状况最佳的去索引
         *
         * @param indexingServices
         * @return
         */
        @Override
        public String select(Map<String, ClusterEntity> indexingServices) throws Exception {
            Iterator<Map.Entry<String, ClusterEntity>> iterator = indexingServices.entrySet().iterator();
            final List<ServerCurrentStatus> serverCurrentStatuses = new ArrayList<ServerCurrentStatus>();
            final CountDownLatch latch = new CountDownLatch(indexingServices.size());
            ProxyFactory factory = ProxySelector.getInstance().getFactory();
            while (iterator.hasNext()) {
                Map.Entry<String, ClusterEntity> entityEntry = iterator.next();
                Invoker invoker = factory.getInvoker(null, entityEntry.getValue().getServiceClass(), new RPCUrl(new RPCDestination(entityEntry.getKey())), false);
                Method method = entityEntry.getValue().getServiceClass().getMethod("currentStatus");
                Result result = invoker.invoke(new SwiftInvocation(method, null));
                RpcFuture future = (RpcFuture) result.getValue();
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        serverCurrentStatuses.add((ServerCurrentStatus) result);
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        SwiftLoggers.getLogger(IndexingSelectRule.class).error(e);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            if (serverCurrentStatuses.isEmpty()) {
                Iterator<String> keyIterator = indexingServices.keySet().iterator();
                if (keyIterator.hasNext()) {
                    return keyIterator.next();
                }
                return null;
            }
            Collections.sort(serverCurrentStatuses);
            return serverCurrentStatuses.get(0).getClusterId();
        }
    };

    String select(Map<String, ClusterEntity> indexingServices) throws Exception;

}
