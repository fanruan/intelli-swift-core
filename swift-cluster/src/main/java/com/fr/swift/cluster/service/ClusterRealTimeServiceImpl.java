package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.cluster.SwiftClusterService;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/6
 */
@Service
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = RealtimeService.class)
public class ClusterRealTimeServiceImpl extends AbstractSwiftService implements ClusterRealTimeService, Serializable {
    private static final long serialVersionUID = 946204307880678794L;
    @Autowired
    private transient RpcServer server;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    @Autowired(required = false)
    private RealtimeService realtimeService;

    @Override
    public boolean start() throws SwiftServiceException {
        realtimeService.start();
        return super.start();
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {

        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.INSERT) {
            @Override
            public void doJob() throws Exception {
                rpcSegmentLocation(PushSegLocationRpcEvent.fromSourceKey(getServiceType(), Collections.singletonList(tableKey.getId())));
                SwiftDatabase.getInstance().getTable(tableKey).insert(resultSet);
            }
        });
    }

    @Override
    @RpcMethod(methodName = "realtimeDelete")
    public boolean delete(SourceKey sourceKey, Where where) throws Exception {
        return realtimeService.delete(sourceKey, where);
    }

    @Override
    @RpcMethod(methodName = "recover")
    public void recover(List<SegmentKey> tableKeys) throws Exception {
        realtimeService.recover(tableKeys);
    }

    @Override
    @RpcMethod(methodName = "realTimeQuery")
    public SwiftResultSet query(String queryInfo) throws Exception {
        return realtimeService.query(queryInfo);
    }

    @Override
    public ServiceType getServiceType() {
        return realtimeService.getServiceType();
    }

    private void rpcSegmentLocation(PushSegLocationRpcEvent event) {
        URL masterURL = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{event}));
        RpcFuture future = (RpcFuture) result.getValue();
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                logger.info("rpcTrigger success! ");
            }

            @Override
            public void fail(Exception e) {
                logger.error("rpcTrigger error! ", e);
            }
        });
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().
                getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}
