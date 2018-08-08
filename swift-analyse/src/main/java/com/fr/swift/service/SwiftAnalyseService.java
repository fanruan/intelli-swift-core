package com.fr.swift.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@Service("analyse")
@RpcService(value = AnalyseService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {
    private static final long serialVersionUID = 841582089735823794L;

    private transient static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseService.class);

    @Autowired
    private transient RpcServer server;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public SwiftAnalyseService(String id) {
        super(id);
    }

    private SwiftAnalyseService() {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        return start;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }

    @Override
    public SwiftResultSet getRemoteQueryResult(final String jsonString, final SegmentDestination remoteURI) {
        final SwiftResultSet[] resultSet = new SwiftResultSet[1];
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            queryRemoteNodeNode(jsonString, remoteURI).addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {
                    resultSet[0] = (SwiftResultSet) result;
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    List<String> spareNodes = remoteURI.getSpareNodes();
                    try {
                        for (String spareNode : spareNodes) {
                            SegmentDestinationImpl spare = new SegmentDestinationImpl(remoteURI);
                            spare.setClusterId(spareNode);
                            final CountDownLatch count = new CountDownLatch(1);
                            queryRemoteNodeNode(jsonString, spare).addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    resultSet[0] = (SwiftResultSet) result;
                                    count.countDown();
                                }

                                @Override
                                public void fail(Exception e) {
                                    count.countDown();
                                }
                            });
                            count.await();
                            if (null != resultSet[0]) {
                                latch.countDown();
                                break;
                            }
                        }
                        if (resultSet[0] == null) {
                            latch.countDown();
                        }
                    } catch (Exception e1) {
                        LOGGER.error("Query remote node error! ", e1);
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            LOGGER.error("Query remote node error! ", e);
        }
        return resultSet[0];
    }

    private RpcFuture queryRemoteNodeNode(String jsonString, SegmentDestination remoteURI) throws Exception {
        if (remoteURI == null) {
            QueryBean bean = QueryInfoBeanFactory.create(jsonString);
            remoteURI = bean.getQueryDestination();
        }
        String address = remoteURI.getAddress();
        String methodName = remoteURI.getMethodName();
        Class clazz = remoteURI.getServiceClass();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, clazz, new RPCUrl(new RPCDestination(address)), false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName(methodName), new Object[]{jsonString}));
        RpcFuture future = (RpcFuture) result.getValue();
        if (null == future) {
            throw new Exception(result.getException());
        }
        return future;
    }

    @Override
    @RpcMethod(methodName = "updateSegmentInfo")
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }
}
