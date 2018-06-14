package com.fr.swift.service.handler.indexing;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.indexing.rule.IndexingSelectRule;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler implements Handler<AbstractIndexingRpcEvent> {

    private IndexingSelectRule rule = IndexingSelectRule.DEFAULT;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftIndexingEventHandler.class);

    public void setRule(IndexingSelectRule rule) {
        this.rule = rule;
    }

    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        Map<String, ClusterEntity> indexingServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
        if (indexingServices.isEmpty()) {
            throw new RuntimeException("Cannot find any Indexing Service!");
        }
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        switch (event.subEvent()) {
            case INDEX:
                try {
                    final long start = System.currentTimeMillis();
                    String address = rule.select(indexingServices);
                    ClusterEntity entity = indexingServices.get(address);
                    Invoker invoker = factory.getInvoker(null, entity.getServiceClass(), new RPCUrl(new RPCDestination(address)), false);
                    Method method = entity.getServiceClass().getMethod("index", IndexingStuff.class);
                    Result result = invoker.invoke(new SwiftInvocation(method, new Object[]{event.getContent()}));
                    RpcFuture rpcFuture = (RpcFuture) result.getValue();
                    rpcFuture.addCallback(new AsyncRpcCallback() {
                        @Override
                        public void success(Object result) {
                            LOGGER.info(String.format("Indexing success! Cost: %d", System.currentTimeMillis() - start));
                        }

                        @Override
                        public void fail(Exception e) {
                            LOGGER.error("Indexing error! ", e);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.error("Indexing error! ", e);
                }
        }
        return null;
    }
}
